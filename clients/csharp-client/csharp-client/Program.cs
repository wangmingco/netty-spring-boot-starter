using System;
using System.Diagnostics;
using System.IO;
using System.Net.Sockets;
using Co.Wangming.Nsb.Samples.Protobuf;
using Google.Protobuf;

namespace csharpclient
{
    class MainClass
    {
        private static string IP = "tcp.nsbs.xyz";
        private static int PORT = 7800;

        public static void Main(string[] args)
        {
            SearchRequest searchRequest = new SearchRequest {
                PageNumber = 1,
                Query = "data from c# client"
            };

            byte[] msg = Serizlize(searchRequest);

            for(;;)
            {
                sendMessage(msg, 1);
                sendMessage(msg, 2);
                sendMessage(msg, 3);
                sendMessage(msg, 4);
                sendMessage(msg, 5);
                sendMessage(msg, 6);
            }
        }

        public static byte[] Serizlize(SearchRequest searchRequest)
        {
            try
            {
                using (MemoryStream ms = new MemoryStream())
                {
                    searchRequest.WriteTo(ms);
                    return ms.ToArray();
                }
            }
            catch (Exception ex)
            {
                Debug.Write("序列化失败: " + ex.ToString());
                return null;
            }
        }

        public static void sendMessage(byte[] msg, byte messageId)
        {
            using (MemoryStream ms = new MemoryStream())
            {
                ms.WriteByte(messageId);
                ms.WriteByte(Convert.ToByte(msg.Length));
                ms.Write(msg, 0, msg.Length);

                Socket clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                try
                {
                    clientSocket.Connect(IP, PORT); //配置服务器IP与端口  
                    Console.WriteLine("连接服务器成功");
                }
                catch
                {
                    Console.WriteLine("连接服务器失败，请按回车键退出！");
                    return;
                }

                int result = clientSocket.Send(ms.ToArray());
                Console.WriteLine("发送数据完成 " + result);
            }
        }

    }
}
