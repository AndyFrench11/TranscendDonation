﻿using System;
using System.Collections.Generic;
using System.Linq;
using CoreGraphics;
using CustomRenderer.iOS;
using Foundation;
using MapKit;
using UIKit;

namespace mobileAppClient.iOS
{
    public class TableSource : UITableViewSource
    {
        public string CellIdentifier = "TableCell";
        public BottomSheetViewController owner;
        public CustomPin pin;
        public string[] organs;
        public int userId;
        public CustomMap map;
        public MKMapView nativeMap;
        public Dictionary<string, UITableViewCell> Cells;
        public CustomMapRenderer customMapRenderer;

        public TableSource(CustomPin pin, CustomMap map, MKMapView nativeMap, BottomSheetViewController owner, CustomMapRenderer customMapRenderer)
        {
            this.nativeMap = nativeMap;
            this.map = map;
            this.pin = pin;
            this.owner = owner;
            organs = pin.Url.Split(',');
            userId = Int32.Parse(organs[organs.Length - 1]);
            organs = organs.Take(organs.Length - 1).ToArray();
            Cells = new Dictionary<string, UITableViewCell>();
            this.customMapRenderer = customMapRenderer;
        }


        public override nint RowsInSection(UITableView tableview, nint section)
        {
            return organs.Length;
        }

        public override UITableViewCell GetCell(UITableView tableView, NSIndexPath indexPath)
        {


            string photoItem = organs[indexPath.Row];
            string item = organs[indexPath.Row].Replace("_icon.png", "");
            item = char.ToUpper(item[0]) + item.Substring(1);

            if (Cells.ContainsKey(item.ToLower()))
            {
                return Cells[item.ToLower()];
            }

            UITableViewCell cell = new UITableViewCell(UITableViewCellStyle.Subtitle, CellIdentifier); ;

            //---- if there are no cells to reuse, create a new one
            //if (cell == null)
            //{ cell = new UITableViewCell(UITableViewCellStyle.Subtitle, CellIdentifier); }

            cell.TextLabel.Text = item;
            cell.BackgroundColor = UIColor.Clear;
            cell.TextLabel.TextColor = UIColor.White;
            cell.ImageView.Image = UIImage.FromFile(photoItem);
            cell.Accessory = UITableViewCellAccessory.DisclosureIndicator;
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN
             //Get organ object from organ list             DonatableOrgan currentOrgan = null;             foreach(DonatableOrgan donatableOrgan in pin.donatableOrgans) {                 if(donatableOrgan.organType.ToLower().Equals(item.ToLower())) {                     currentOrgan = donatableOrgan;                 }             }             string countdownDetail = "";             if (currentOrgan.expired)             {
                cell.DetailTextLabel.Text = "EXPIRED";                 cell.DetailTextLabel.TextColor = UIColor.Red;             } else if(currentOrgan.inTransfer == 1) {
                cell.DetailTextLabel.Text = "IN TRANSIT";
                cell.DetailTextLabel.TextColor = UIColor.Orange;
            } else if(currentOrgan.inTransfer == 2) {
                cell.DetailTextLabel.Text = "SUCCESSFULLY TRANSFERRED";
                cell.DetailTextLabel.TextColor = UIColor.Green;
            }             else             {                 Tuple<string, long> timeRemainingTuple = currentOrgan.getTimeRemaining();                 cell.DetailTextLabel.Text = timeRemainingTuple.Item1;                 //Change colour based on severity
                long timeRemaining = timeRemainingTuple.Item2;                 if(timeRemaining <= 3600) {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(244, 65, 65);
                } else if(timeRemaining <= 10800) {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(244, 130, 65);
                } else if(timeRemaining <= 21600) {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(244, 190, 65);
                }
                else if (timeRemaining <= 43200)
                {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(244, 241, 65);
                }
                else if (timeRemaining <= 86400)
                {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(208, 244, 65);
                }
                else if (timeRemaining <= 172800)
                {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(160, 244, 65);
                } else {
                    cell.DetailTextLabel.TextColor = UIColor.FromRGB(76, 244, 65);
                }              }

            if(!(Cells.ContainsKey(item.ToLower()))) {
                Cells.Add(item.ToLower(), cell);
            }
            return cell;
        }

        public void removeOverlays()
        {
            if (nativeMap.Overlays != null && nativeMap.Overlays.Length > 0)
            {
                nativeMap.Overlays[0].Dispose();
                nativeMap.RemoveOverlay(nativeMap.Overlays[0]);

            }

        }

        public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
        {
            removeOverlays();
            owner.StopTimers();

            string organ = organs[indexPath.Row].Replace("_icon.png", "");

            UITableViewCell currentOrganCell = Cells[organ];

            var potentialRecipientsController = new PotentialMatchesBottomSheetViewController(pin, map, nativeMap, organ, currentOrganCell, customMapRenderer);
            
            var window = UIApplication.SharedApplication.KeyWindow;

            potentialRecipientsController.AddChildViewController(owner.ChildViewControllers[0]);

            window.RootViewController = potentialRecipientsController;




        }
    }
}
