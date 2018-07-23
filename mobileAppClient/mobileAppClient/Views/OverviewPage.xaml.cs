﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class OverviewPage : ContentPage, UserObserver
    {
        public OverviewPage()
        {
            InitializeComponent();
            UserController.Instance.addUserObserver(this);
            if (UserController.Instance.LoggedInUser != null)
            {
                fillFields();
            }

        }

        public void updateUser()
        {
            fillFields();
        }

        public void fillFields() {
            
            User currentUser = UserController.Instance.LoggedInUser;


            //Attributes Pane 
            //---------------------------------------------------------------------------
            double age;
            if (currentUser.dateOfDeath == null)
            {
                age = (DateTime.Now - currentUser.dateOfBirth.ToDateTime()).Days / 365.00;
            }
            else
            {
                age = (currentUser.dateOfDeath.ToDateTime() - currentUser.dateOfBirth.ToDateTime()).Days / 365.00;
            }
            string attributesString = "Name: " + String.Join(" ", currentUser.name) + "\nAge: " + String.Format("{0:0.00}", age) + " years old";
            if (currentUser.currentAddress != null) attributesString += "\nCurrent Address: " + currentUser.currentAddress;
            if (currentUser.gender.ToString() != "") attributesString += "\nBirth Gender: " + currentUser.gender.ToString();
            AttributesLabel.Text = attributesString;

            //Organs Pane 
            //---------------------------------------------------------------------------
            string organsString = "";
            if (currentUser.organs.Count == 0)
            {
                organsString = "No organs currently donated.";
            }
            else
            {
                organsString = "Currently donating: \n" + String.Join(", ", currentUser.organs);
            }
            OrgansLabel.Text = organsString;


            //Medications Pane 
            //---------------------------------------------------------------------------
            string currentMedicationsString = "";
            if (currentUser.currentMedications.Count == 0)
            {
                currentMedicationsString = "No medications currently being taken.";
            }
            else
            {
                List<string> currentMedications = new List<string>();
                foreach (Medication item in currentUser.currentMedications)
                {
                    currentMedications.Add(item.Name);
                }
                currentMedicationsString = "Currently taking: \n" + String.Join(", ", currentMedications);
            }

            string historicMedicationsString = "";
            if (currentUser.historicMedications.Count == 0)
            {
                historicMedicationsString = "No medications have been taken in the past.";
            }
            else
            {
                List<string> historicMedications = new List<string>();
                foreach (Medication item in currentUser.historicMedications)
                {
                    historicMedications.Add(item.Name);
                }
                historicMedicationsString = "Has taken: \n" + String.Join(", ", historicMedications);
            }

            MedicationsLabel.Text = currentMedicationsString + "\n" + historicMedicationsString;


            //Diseases Pane 
            //---------------------------------------------------------------------------
            string currentDiseasesString = "";
            if (currentUser.currentDiseases.Count == 0)
            {
                currentDiseasesString = "Not suffering from any diseases currently.";
            }
            else
            {
                List<string> currentDiseases = new List<string>();
                foreach (Disease item in currentUser.currentDiseases)
                {
                    currentDiseases.Add(item.Name);
                }
                currentDiseasesString = "Currently suffering from: \n" + String.Join(", ", currentDiseases);
            }

            string curedDiseasesString = "";
            if (currentUser.curedDiseases.Count == 0)
            {
                curedDiseasesString = "Has not suffered from any diseases in the past.";
            }
            else
            {
                List<string> curedDiseases = new List<string>();
                foreach (Disease item in currentUser.curedDiseases)
                {
                    curedDiseases.Add(item.Name);
                }
                curedDiseasesString = "Has suffered from: \n" + String.Join(", ", curedDiseases);
            }


            DiseasesLabel.Text = currentDiseasesString + "\n" + curedDiseasesString;

            //Procedures Pane 
            //---------------------------------------------------------------------------

            string pendingProceduresString = "";
            if (currentUser.pendingProcedures.Count == 0)
            {
                pendingProceduresString = "Not due for any procedures in the future.";
            }
            else
            {
                List<string> pendingProcedures = new List<string>();
                foreach (Procedure item in currentUser.pendingProcedures)
                {
                    pendingProcedures.Add(item.Summary);
                }
                pendingProceduresString = "Procedures due for: \n" + String.Join(", ", pendingProcedures);
            }

            string previousProceduresString = "";
            if (currentUser.previousProcedures.Count == 0)
            {
                previousProceduresString = "Has not had any procedures in the past.";
            }
            else
            {
                List<string> previousProcedures = new List<string>();
                foreach (Procedure item in currentUser.previousProcedures)
                {
                    previousProcedures.Add(item.Summary);
                }
                previousProceduresString = "Has had the following procedures: \n" + String.Join(", ", previousProcedures);
            }

            ProceduresLabel.Text = pendingProceduresString + "\n" + previousProceduresString;

            //Waiting List Items Pane 
            //---------------------------------------------------------------------------

            string waitingListItemsString = "";
            if (currentUser.waitingListItems.Count == 0)
            {
                waitingListItemsString = "Not waiting on any organs to be donated.";
            }
            else
            {
                List<string> waitingListItems = new List<string>();
                foreach (WaitingListItem item in currentUser.waitingListItems)
                {
                    waitingListItems.Add(item.OrganType);
                }
                waitingListItemsString = "Organs waiting on: \n" + String.Join(", ", waitingListItems);
            }

            WaitingListLabel.Text = waitingListItemsString;

        
        }
    }
}
