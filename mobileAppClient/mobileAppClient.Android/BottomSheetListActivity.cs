﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Support.V7.App;
using Android.Graphics.Drawables;
using Android.Graphics;
using Android.Support.V4.Graphics.Drawable;
using ServiceStack;

namespace mobileAppClient.Droid
{
    [Activity]
    public class BottomSheetListActivity : AppCompatActivity
    {
        List<DonatableOrgan> organs;
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.OrganTransfer);

            PrepareSheet();

        }

        public void PrepareSheet()
        {
            /* TODO:
                - Countdowns
                - Hospital support
                - Recipient support
            */
            var name = FindViewById<TextView>(Resource.Id.User_Name);
            var address = FindViewById<TextView>(Resource.Id.Address);
            var profilePicture = FindViewById<ImageView>(Resource.Id.ProfilePictureFrame);
            var organTable = FindViewById<TableLayout>(Resource.Id.organTableLayout);


            if (name != null)
            {
                name.Text = Intent.GetStringExtra("name");
            }
            if (address != null)
            {
                address.Text = Intent.GetStringExtra("address");
            }
            if (profilePicture != null)
            {
                var pictureString = Intent.GetStringExtra("profilePicture");
                if (pictureString.Length == 0)
                {
                    profilePicture.SetImageResource(Resource.Drawable.donationIcon);
                }
                else
                {
                    var imageBytes = Convert.FromBase64String(pictureString);
                    var imageData = BitmapFactory.DecodeByteArray(imageBytes, 0, imageBytes.Length);
                    profilePicture.SetImageBitmap(imageData);
                }
            }

            var organString = Intent.GetStringExtra("organs");

            organs = organString.FromJson<List<DonatableOrgan>>();
            
            foreach (DonatableOrgan organ in organs)
            {
                TableRow organRow = new TableRow(this);
                TextView organText = new TextView(this);
                ImageView organImage = new ImageView(this);
                String organName = organ.organType;

                switch (organ.organType.ToLower())
                {
                    case "bone-marrow":
                        organImage.SetImageResource(Resource.Drawable.bone_icon);
                        organText.Text = "Bone Marrow";
                        break;
                    case "middle-ear":
                        organImage.SetImageResource(Resource.Drawable.ear_icon);
                        organText.Text = "Middle Ear";
                        break;
                    case "cornea":
                        organImage.SetImageResource(Resource.Drawable.eye_icon);
                        organText.Text = "Cornea";
                        break;
                    case "heart":
                        organImage.SetImageResource(Resource.Drawable.heart_icon);
                        organText.Text = "Heart";
                        break;
                    case "intestines":
                        organImage.SetImageResource(Resource.Drawable.intestines_icon);
                        organText.Text = "Intestines";
                        break;
                    case "kidney":
                        organImage.SetImageResource(Resource.Drawable.kidney_icon);
                        organText.Text = "Kidney";
                        break;
                    case "liver":
                        organImage.SetImageResource(Resource.Drawable.liver_icon);
                        organText.Text = "Liver";
                        break;
                    case "lungs":
                        organImage.SetImageResource(Resource.Drawable.lungs_icon);
                        organText.Text = "Lungs";
                        break;
                    case "pancreas":
                        organImage.SetImageResource(Resource.Drawable.pancreas_icon);
                        organText.Text = "Pancreas";
                        break;
                    case "skin":
                        organImage.SetImageResource(Resource.Drawable.skin_icon);
                        organText.Text = "Skin";
                        break;
                    case "connective-tissue":
                        organImage.SetImageResource(Resource.Drawable.tissue_icon);
                        organText.Text = "Connective Tissue";
                        break;
                }
                organText.SetTextAppearance(this, Android.Resource.Style.TextAppearanceMedium);

                organImage.SetAdjustViewBounds(true);
                organImage.SetMaxHeight(80);
                organImage.SetMaxWidth(80);
                organImage.SetPadding(5, 0, 20, 0);
               // organRow.Click += (sender, e) =>
               // {
              //      transferOrgan(organs, organName, organTable.IndexOfChild(organRow));
              //  };
                

                organRow.AddView(organImage);
                organRow.AddView(organText);
                organTable.AddView(organRow);

            }
               

        }

        private void transferOrgan(List<DonatableOrgan> organs, String organName, int index)
        {
            var organTable = FindViewById<TableLayout>(Resource.Id.organTableLayout);
            TableLayout recipientTable = new TableLayout(this);
            //Get list of recipients
            //Iterate and create rows for each

            foreach(DonatableOrgan organ in organs)
            {
                if (organName.Equals(organ.organType))
                {
                    foreach (User recipient in organ.topReceivers)
                    {
                        TableRow recipientRow = new TableRow(this);
                        TextView recipientName = new TextView(this);

                        recipientName.Text = recipient.FullName;

                        recipientRow.AddView(recipientName);
                    }
                }
            }

            //Uncommenting causes the tableview not to display proper
            //organTable.AddView(recipientTable, index);
        }

    }

   
}
