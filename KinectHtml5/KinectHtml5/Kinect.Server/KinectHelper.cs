using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization.Json;
using Microsoft.Research.Kinect.Nui;
using System.IO;
using System.Text;
using System.Runtime.Serialization;

namespace Kinect.Server
{
    public static class KinectHelper
    {
        public static Joint ScaleTo(this Joint joint, int width, int height, float skeletonMaxX, float skeletonMaxY)
        {
            Vector pos = new Vector()
            {
                X = joint.Position.X *1000f,
                Y = joint.Position.Y * 1000f,
                Z = joint.Position.Z * 1000f,
                W = joint.Position.W
            };

            Joint j = new Joint()
            {
                ID = joint.ID,
                TrackingState = joint.TrackingState,
                Position = pos
            };

            return j;
        }

        public static Joint ScaleTo(this Joint joint, int width, int height)
        {
            return ScaleTo(joint, width, height, 1.0f, 1.0f);
        }
    }
}
