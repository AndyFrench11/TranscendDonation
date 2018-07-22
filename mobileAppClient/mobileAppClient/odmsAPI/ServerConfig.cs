﻿using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.NetworkInformation;
using System.Text;
using System.Threading.Tasks;

namespace mobileAppClient.odmsAPI
{
    /*
     * Provides a singleton container that tracks the current server address and holds a single HttpClient to be used across API calls
     */
    sealed class ServerConfig
    {
        public String serverAddress { get; set; }

        public HttpClient client;

        private static readonly Lazy<ServerConfig> lazy =
        new Lazy<ServerConfig>(() => new ServerConfig());

        public static ServerConfig Instance { get { return lazy.Value; } }

        private ServerConfig()
        {
            client = new HttpClient();

            // Sets default addr -> needed for register pane
            serverAddress = "http://csse-s302g3.canterbury.ac.nz:80/api/v1";
        }

        public async Task<bool> IsConnectedToInternet()
        {
            Ping p = new Ping();
            PingReply reply = await p.SendPingAsync("1.1.1.1");

            if (reply.Status == IPStatus.Success)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
