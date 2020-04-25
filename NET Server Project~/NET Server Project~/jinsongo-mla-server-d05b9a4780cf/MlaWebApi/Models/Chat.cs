using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace MlaWebApi.Models
{
    public class Chat
    {
        //
        // GET: /Chat/
       // public int messageId { get; set; }
        public DateTime messagedate { get; set; }
        public string text { get; set; }
        public string receiver { get; set; }
        public string sender { get; set; }


    }
}
