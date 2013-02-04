using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Fleck;
using Microsoft.Research.Kinect.Nui;

namespace Kinect.Server
{
    class Program
    {
        static Runtime nui;
        static List<IWebSocketConnection> sockets;

        static bool initialized = false;

        static void Main(string[] args)
        {
            if (Runtime.Kinects.Count <= 0) return;

            InitilizeKinect();
            InitializeSockets();
        }


        /// <summary>
        /// 
        /// </summary>
        private static void InitializeSockets()
        {
            sockets = new List<IWebSocketConnection>();

            var server = new WebSocketServer("ws://localhost:8181");

            server.Start(socket =>
            {

                socket.OnOpen = () =>
                {
                    Console.WriteLine("Connected to " + socket.ConnectionInfo.ClientIpAddress);
                    sockets.Add(socket);
                };


                socket.OnClose = () =>
                {
                    Console.WriteLine("Disconnected from " + socket.ConnectionInfo.ClientIpAddress);
                    sockets.Remove(socket);
                };

                
                socket.OnMessage = message =>
                {
                    Console.WriteLine(message);
                };
            });

            initialized = true;

            Console.ReadLine();
        }

        /// <summary>
        /// 
        /// </summary>
        private static void InitilizeKinect()
        {
            nui = Runtime.Kinects[0];
            nui.Initialize(RuntimeOptions.UseDepthAndPlayerIndex | RuntimeOptions.UseSkeletalTracking);
            nui.SkeletonFrameReady += new EventHandler<SkeletonFrameReadyEventArgs>(Nui_SkeletonFrameReady);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        static void Nui_SkeletonFrameReady(object sender, SkeletonFrameReadyEventArgs e)
        {
            if (!initialized) return;

            List<SkeletonData> users = new List<SkeletonData>();

            foreach (var user in e.SkeletonFrame.Skeletons)
            {
                if (user.TrackingState == SkeletonTrackingState.Tracked)
                {
                    users.Add(user);
                }
            }

            if (users.Count > 0)
            {
                string json = users.Serialize();

                foreach (var socket in sockets)
                {
                    socket.Send(json);
                }
            }
        }
    }
}
