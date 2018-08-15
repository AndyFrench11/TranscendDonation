﻿using System;
using System.Collections.Generic;
using System.Linq;

using Xamarin.Forms;
using System.Globalization;
using mobileAppClient.odmsAPI;
using System.Threading.Tasks;
using System.Net;
using mobileAppClient.Models;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the waitingListItems page for 
     * showing all of a users waiting list items.
     */ 
    public partial class WaitingListItemsPage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
        /*
         * Constructor which sets the detail strings for each text cell 
         * and also sets the visibility of the no data label and sorting picker.
         */ 
        public WaitingListItemsPage()
        {
            InitializeComponent();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (WaitingListItem item in UserController.Instance.LoggedInUser.waitingListItems)
            {
                item.DetailString = "Registered on " + item.organRegisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(item.organRegisteredDate.month) + ", " + item.organRegisteredDate.year;
                if(item.organDeregisteredDate != null) {
                    item.DetailString = "Deregistered on " + item.organDeregisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(item.organDeregisteredDate.month) + ", " + item.organDeregisteredDate.year;
                } 
            }

            if (UserController.Instance.LoggedInUser.waitingListItems.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                WaitingListItemsList.IsVisible = false;
                SortingInput.IsVisible = false;
            }

            WaitingListItemsList.ItemsSource = UserController.Instance.LoggedInUser.waitingListItems;
        }

        public async void Handle_RegisterClicked(object sender, EventArgs args)
        {
            String selectedOrgan = (String)OrganPicker.SelectedItem;
            if (selectedOrgan != null)
            {
                
                User user = UserController.Instance.LoggedInUser;
                WaitingListItem newItem = new WaitingListItem();
                String[] words = selectedOrgan.Split(' ');
                String organ = String.Join("-", words);
                organ = organ.ToLower();


                newItem.organType = OrganExtensions.ToOrgan(organ);
                newItem.userId = user.id;
                newItem.organRegisteredDate = new CustomDate(DateTime.Today);

                user.waitingListItems.Add(newItem);

                UserAPI userAPI = new UserAPI();
                HttpStatusCode code = await userAPI.UpdateUser(user, ClinicianController.Instance.AuthToken);

                switch (code)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                            "User successfully updated",
                            "OK");
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                            "User update failed (400)",
                            "OK");
                        break;
                    case HttpStatusCode.ServiceUnavailable:
                        await DisplayAlert("",
                            "Server unavailable, check connection",
                            "OK");
                        break;
                    case HttpStatusCode.InternalServerError:
                        await DisplayAlert("",
                            "Server error, please try again",
                            "OK");
                        break;
                }
            }
        }


        /*
         * Handles when a single waiting list item is tapped, sending a user to the single waiting list item page 
         * of that given waiting list item.
         */ 
        async void Handle_WaitingListItemTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleWaitingListItemPage = new SingleWaitingListItemPage((WaitingListItem)WaitingListItemsList.SelectedItem, false);
            await Navigation.PushModalAsync(singleWaitingListItemPage);
        }

        /*
         * Handles when a user selects a given attribute of the sorting dropdown 
         * to sort by, sorting the given items in the list view.
         */ 
        void Handle_SelectedIndexChanged(object sender, System.EventArgs e)
        {
            List<WaitingListItem> currentList = (System.Collections.Generic.List<WaitingListItem>)WaitingListItemsList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Organ":
                    
                    List<WaitingListItem> SortedList = currentList.OrderBy(o => o.organType).ToList();
                    WaitingListItemsList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Reg. Date":
                    SortedList = currentList.OrderBy(o => o.organRegisteredDate.ToDateTime()).ToList();
                    WaitingListItemsList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Dereg. Date":

                    List<WaitingListItem> finalList = new List<WaitingListItem>();
                    List<WaitingListItem> deregisteredList = new List<WaitingListItem>();
                    List<WaitingListItem> nonDeregisteredList = new List<WaitingListItem>();

                    foreach (WaitingListItem item in currentList)
                    {
                        if (item.organDeregisteredDate != null)
                        {
                            deregisteredList.Add(item);
                        }
                        else
                        {
                            nonDeregisteredList.Add(item);
                        }
                    }

                    List<WaitingListItem> sortedDeregisteredList = deregisteredList.OrderBy(o => o.organDeregisteredDate.ToDateTime()).ToList();

                    foreach (WaitingListItem item in sortedDeregisteredList)
                    {
                        finalList.Add(item);
                    }
                    foreach (WaitingListItem item in nonDeregisteredList)
                    {
                        finalList.Add(item);
                    }

                    WaitingListItemsList.ItemsSource = finalList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Dereg. Code":
                    SortedList = currentList.OrderBy(o => o.organDeregisteredCode).ToList();
                    WaitingListItemsList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Clear":

                    WaitingListItemsList.ItemsSource = currentList;                        
                    SortingInput.SelectedIndex = -1;

                    AscendingDescendingPicker.IsVisible = false;
                    break;
            }
        }

        /*
         * Handles when a user changes the orientation of the sorting to be either ascending or 
         * descending, changing the order in which items are sorted in the list view.
         */ 
        void Handle_UpDownChanged(object sender, System.EventArgs e)
        {
            List<WaitingListItem> currentList = (System.Collections.Generic.List<WaitingListItem>)WaitingListItemsList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Organ":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.organType).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.organType).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
                case "Reg. Date":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.organRegisteredDate.ToDateTime()).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.organRegisteredDate.ToDateTime()).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;

                            break;
                    }
                    break;
                case "Dereg. Date":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> finalList = new List<WaitingListItem>();
                            List<WaitingListItem> deregisteredList = new List<WaitingListItem>();
                            List<WaitingListItem> nonDeregisteredList = new List<WaitingListItem>();

                            foreach (WaitingListItem item in currentList)
                            {
                                if (item.organDeregisteredDate != null)
                                {
                                    deregisteredList.Add(item);
                                }
                                else
                                {
                                    nonDeregisteredList.Add(item);
                                }
                            }

                            List<WaitingListItem> sortedDeregisteredList = deregisteredList.OrderByDescending(o => o.organDeregisteredDate.ToDateTime()).ToList();

                            foreach (WaitingListItem item in sortedDeregisteredList)
                            {
                                finalList.Add(item);
                            }
                            foreach (WaitingListItem item in nonDeregisteredList)
                            {
                                finalList.Add(item);
                            }

                            WaitingListItemsList.ItemsSource = finalList;

                            break;
                        case "⬇ (Ascending)":
                            finalList = new List<WaitingListItem>();
                            deregisteredList = new List<WaitingListItem>();
                            nonDeregisteredList = new List<WaitingListItem>();

                            foreach (WaitingListItem item in currentList)
                            {
                                if (item.organDeregisteredDate != null)
                                {
                                    deregisteredList.Add(item);
                                }
                                else
                                {
                                    nonDeregisteredList.Add(item);
                                }
                            }

                            sortedDeregisteredList = deregisteredList.OrderBy(o => o.organDeregisteredDate.ToDateTime()).ToList();

                            foreach (WaitingListItem item in sortedDeregisteredList)
                            {
                                finalList.Add(item);
                            }
                            foreach (WaitingListItem item in nonDeregisteredList)
                            {
                                finalList.Add(item);
                            }

                            WaitingListItemsList.ItemsSource = finalList;

                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;

                            break;
                    }
                    break;
                case "Dereg. Code":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.organDeregisteredCode).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.organDeregisteredCode).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
            }      
        }
    }
}
