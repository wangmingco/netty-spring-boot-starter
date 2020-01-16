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
        public static void Main(string[] args)
        {
            SearchRequest searchRequest = new SearchRequest {
                PageNumber = 1,
                Query = "123"
            };

            byte[] msg = Serizlize(searchRequest);

            sendMessage(msg);

        }

        public static byte[] Serizlize(SearchRequest meg)
        {
            try
            {
                //涉及格式转换，需要用到流，将二进制序列化到流中
                using (MemoryStream ms = new MemoryStream())
                {
                    //使用ProtoBuf工具的序列化方法
                    meg.WriteTo(ms);

                    return ms.ToArray();
                    ////定义二级制数组，保存序列化后的结果
                    //byte[] result = new byte[ms.Length];
                    ////将流的位置设为0，起始点
                    //ms.Position = 0;
                    ////将流中的内容读取到二进制数组中
                    //ms.Read(result, 0, result.Length);

                    //return result;
                }
            }
            catch (Exception ex)
            {
                Debug.Write("序列化失败: " + ex.ToString());
                return null;
            }
        }

        public static void sendMessage(byte[] msg)
        {
            Socket clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                clientSocket.Connect("tcp.nsbs.xyz", 7800); //配置服务器IP与端口  
                Console.WriteLine("连接服务器成功");
            }
            catch
            {
                Console.WriteLine("连接服务器失败，请按回车键退出！");
                return;
            }

            int result = clientSocket.Send(msg);
            Console.WriteLine("发送数据完成 " + result);
        }

    }
}
