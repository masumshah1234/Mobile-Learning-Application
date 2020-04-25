using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MlaWebApi.Models;
using System.Configuration;
//using System.Data.SqlServerCe;
using System.Data;
using System.Data.SqlClient;
using System.Data.Entity;

namespace MlaWebApi.Controllers
{
    public class ChatController : ApiController
    {
        public string cfmgr = ConfigurationManager.ConnectionStrings["Mladb"].ConnectionString;
        SqlConnection cnn = null;

        public IEnumerable<Chat> GetAllChat(string receiver)
        {
            cnn = new SqlConnection(cfmgr);
            cnn.Open();

            SqlCommand comm = new SqlCommand("Select messagedate, text, receiver, sender from chat where receiver = '" +receiver+"'", cnn);
            SqlDataAdapter Sqlda = new SqlDataAdapter(comm);
            DataSet dsDatast = new DataSet();
            Sqlda.Fill(dsDatast);

            foreach (DataRow row in dsDatast.Tables[0].Rows)
            {
                yield return new Chat
                {
                    messagedate = DateTime.Parse(Convert.ToString(row["messagedate"])),
                    text = Convert.ToString(row["text"]),
                    receiver = Convert.ToString(row["receiver"]),
                    sender = Convert.ToString(row["sender"])
                };
            }
            cnn.Close();

        }





        public HttpResponseMessage PostChat(DateTime messagedate, string text, string receiver, string sender)
        {

            DataSet dsData = new DataSet("chat");
            cnn = new SqlConnection(cfmgr);
            cnn.Open();

            try
            {
                SqlCommand comm = new SqlCommand("Insert into chat(messagedate,text,receiver,sender) values('"
                    + messagedate
                    + "','" + text
                    + "','" + receiver
                    + "','" + sender
                    + "')", cnn);
                //int countUpdated =comm.ExecuteNonQuery();
                SqlDataAdapter sqlada = new SqlDataAdapter(comm);
                sqlada.Fill(dsData);
                Chat chat = new Chat();

                var response = Request.CreateResponse<Chat>(System.Net.HttpStatusCode.Found, chat);
                cnn.Close();
                return response;
            }
            catch (Exception e)
            {
                Chat ct = new Chat();
                var response = Request.CreateResponse<Chat>(System.Net.HttpStatusCode.BadRequest, ct);
                cnn.Close();
                return response;
            }

        }
    }
}