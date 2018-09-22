﻿using mobileAppClient.odmsAPI.RequestFormat;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;


namespace mobileAppClient.odmsAPI
{
    /*
     * Holds methods that interface to the /user endpoint in the ODMS API
     */
    class ClinicianAPI
    {
        /*
         * Returns the status of updating a user object to the server
         */
        public async Task<HttpStatusCode> UpdateClinician()
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            //User History Items are not currently configured thus must send as an empty list.
            //UserController.Instance.LoggedInUser.userHistory = new List<HistoryItem>();

            String registerClinicianRequestBody = JsonConvert.SerializeObject(ClinicianController.Instance.LoggedInClinician);
            HttpContent body = new StringContent(registerClinicianRequestBody);

            Console.WriteLine(registerClinicianRequestBody);

            long clinicianId = ClinicianController.Instance.LoggedInClinician.staffID;

            Console.WriteLine(ClinicianController.Instance.AuthToken);

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/clinicians/" + clinicianId);
            request.Content = body;
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            Console.WriteLine(request);

            try
            {
                var response = await client.SendAsync(request);

                if (response.StatusCode == HttpStatusCode.Created)
                {
                    Console.WriteLine("Success on editing clinician");
                    return HttpStatusCode.Created;
                }
                else
                {
                    Console.WriteLine(String.Format("Failed update clinician ({0})", response.StatusCode));
                    return response.StatusCode;
                }
            }
            catch (HttpRequestException ex)
            {
                Console.WriteLine(ex);
                return HttpStatusCode.ServiceUnavailable;
            }
        }

        /// <summary>
        /// Fetches a single user with a given id
        /// </summary>
        /// <returns>
        /// Tuple containing the HTTP return code and the User object
        /// </returns>
        public async Task<Tuple<HttpStatusCode, Clinician>> GetSingleClinician(string id)
        {
            // Check internet connection
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, Clinician>(HttpStatusCode.ServiceUnavailable, null);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            String queries = null;

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/clinicians/" + id);

            if (ClinicianController.Instance.isLoggedIn())
            {
                request.Headers.Add("token", ClinicianController.Instance.AuthToken);
            }
            else
            {
                request.Headers.Add("token", UserController.Instance.AuthToken);
            }


            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException e)
            {
                return new Tuple<HttpStatusCode, Clinician>(HttpStatusCode.ServiceUnavailable, null);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new Tuple<HttpStatusCode, Clinician>(response.StatusCode, null);
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            Clinician resultUser = JsonConvert.DeserializeObject<Clinician>(responseContent);
            return new Tuple<HttpStatusCode, Clinician>(HttpStatusCode.OK, resultUser);
        }
    }
}
