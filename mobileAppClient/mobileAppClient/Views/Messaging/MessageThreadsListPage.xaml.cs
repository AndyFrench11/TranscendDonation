﻿using System;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using mobileAppClient.Models;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using mobileAppClient.Notifications;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
    /*
     * Page which shows user conversations
     */
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class MessageThreadsListPage : ContentPage
    {
        // One of these will always be null
        private User localUser { get; set; }
        private Clinician localClinician { get; set; }

        private bool isClinicianAccessing { get; set; }

        private List<int> activeConversations;

        public CustomObservableCollection<Conversation> conversationList { get; set; }


        // Loading represents fetching more users at the bottom of the list
        private bool _IsLoading;
        public bool IsLoading
        {
            get { return _IsLoading; }
            set
            {
                _IsLoading = value;
                if (_IsLoading == true)
                {
                    LoadingIndicator.IsVisible = true;
                    LoadingIndicator.IsRunning = true;
                    NewConversationButton.IsVisible = false;
                }
                else
                {
                    LoadingIndicator.IsVisible = false;
                    LoadingIndicator.IsRunning = false;

                    if (isClinicianAccessing)
                    {
                        NewConversationButton.IsVisible = true;
                    }      
                }
            }
        }

        /// <summary>
        /// Constructor for when a user opens their messages
        /// </summary>
        public MessageThreadsListPage()
        {
            InitializeComponent();
            Title = "Messages";

            CheckIfClinicianAccessing();

            conversationList = new CustomObservableCollection<Conversation>();
            activeConversations = new List<int>();

            ConversationsListView.ItemsSource = conversationList;
            VSAppCenter.setConversationListController(this);
        }


        /// <summary>
        /// Activated whenever focus is on this page
        /// </summary>
        protected override async void OnAppearing()
        {
            conversationList.Clear();
            IsLoading = true;
            if (isClinicianAccessing)
            {
                localClinician = ClinicianController.Instance.LoggedInClinician;
                await LoadClinicianConversations();
                
            }
            else
            {
                localUser = UserController.Instance.LoggedInUser;
                await LoadUserConversations();
                NewConversationButton.IsEnabled = false; 
            }
            IsLoading = false;
        }

        /// <summary>
        /// Only called by VSAppCenter if a new conversation is made with a new message
        /// </summary>
        /// <returns></returns>
        public async Task ReloadConversations()
        {
            conversationList.Clear();
            IsLoading = true;
            if (isClinicianAccessing)
            {
                localClinician = ClinicianController.Instance.LoggedInClinician;
                await LoadClinicianConversations();

            }
            else
            {
                localUser = UserController.Instance.LoggedInUser;
                await LoadUserConversations();
                NewConversationButton.IsEnabled = false;
            }
            IsLoading = false;
        }

        protected override void OnDisappearing()
        {
            VSAppCenter.setConversationListController(null);
        }


        /// <summary>
        /// Checks whether the clinician is viewing this page, important for fetching the correct profiles of participants
        /// </summary>
        private void CheckIfClinicianAccessing()
        {
            if (ClinicianController.Instance.isLoggedIn())
            {
                isClinicianAccessing = true;
            }
        }

        /*
         * Navigates to the the conversation if one is tapped
         */
        async void Handle_Conversation_Tapped(object sender, ItemTappedEventArgs e)
        {
            Conversation tappedConversation = (Conversation)e.Item;
            var localId = localUser?.id ?? localClinician.staffID;

            foreach (Message m in tappedConversation.messages) {
                m.SetType(localId);
            }

            await Navigation.PushAsync(new ConversationPage(tappedConversation, localId));
        }

        /*
         * Retrieves all conversations for a clinicians
         */
        private async Task LoadClinicianConversations()
        {
            
            List<Conversation> rawConversations;
            MessagingAPI messagingApi = new MessagingAPI();

            Tuple<HttpStatusCode, List<Conversation>> conversationsFetch = await messagingApi.GetConversations(localClinician.staffID, true);
            switch (conversationsFetch.Item1)
            {
                case HttpStatusCode.OK:
                    rawConversations = conversationsFetch.Item2;
                    break;
                default:
                    await DisplayAlert("", $"Failed to load conversations ({conversationsFetch.Item1})", "OK");
                    return;
            }

            activeConversations.Clear();
            foreach (Conversation currentConversation in rawConversations)
            {
                currentConversation.getParticipantNames(localClinician.staffID);
                activeConversations.Add(currentConversation.externalId);
                conversationList.Add(currentConversation);
            }
        }

        /*
         * Retrieves all conversations for a user
         */
        private async Task LoadUserConversations()
        {
            
            List<Conversation> rawConversations;
            MessagingAPI messagingApi = new MessagingAPI();

            Tuple<HttpStatusCode, List<Conversation>> conversationsFetch = await messagingApi.GetConversations(localUser.id, false);
            switch (conversationsFetch.Item1)
            {
                case HttpStatusCode.OK:
                    rawConversations = conversationsFetch.Item2;
                    break;
                default:
                    await DisplayAlert("", $"Failed to load conversations ({conversationsFetch.Item1})", "OK");
                    return;
            }

            activeConversations.Clear();
            foreach (Conversation currentConversation in rawConversations)
            {
                currentConversation.getParticipantNames(localUser.id);
                activeConversations.Add(currentConversation.externalId);
                conversationList.Add(currentConversation);
            }
        }

        /*
         * Creates a new conversation when the button is tapped
         */
        private async void NewConversationTapped(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new CreateConversationPage(activeConversations));
        }
    }
}
