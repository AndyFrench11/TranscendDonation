﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;

namespace mobileAppClient.Models
{
    public class Conversation
    {
        // Unique conversation ID
        public int id { get; set; }
        public CustomObservableCollection<Message> messages { get; set; }
        public List<int> members { get; set; }

        public string localName { get; set; }
        public string externalName { get; set; }
        public int externalId { get; set; }
        public Dictionary<int, String> memberNames { get; set; }

        public string lastMessage
        {
            get { return messages.Count > 0 ? messages.Last().text : ""; }
        }

        public string lastMessageReceiveTime
        {
            get { return messages.Count > 0 ? messages.Last().timestampDateTime.ToString() : ""; }
        }

        public void getParticipantNames(int localId)
        {
            // Gets the complement of the members list in respect to the local id -> the external participants id
            members.Remove(localId);
            localName = memberNames[localId];

            externalId = members[0];
            externalName = memberNames[members[0]];
        } 
    }
}
