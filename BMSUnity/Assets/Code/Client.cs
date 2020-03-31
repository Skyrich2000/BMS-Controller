using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

public class Client : MonoBehaviour {
    public Socket socket;
    [SerializeField] private string ipAdress = "";
    [SerializeField] private int port = 8090;
    private byte[] msg = new byte[1];

    void Awake() {
        socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

        try {
            IPAddress ipAddr = IPAddress.Parse(ipAdress);
            IPEndPoint ipendPoint = new IPEndPoint(ipAddr, port);
            socket.Connect(ipendPoint);

        } catch (SocketException se) {
            Debug.Log("Socket connect error ! : " + se.ToString());
            return;
        }
    }

    void Update() {
        if (Input.GetKeyDown(KeyCode.Escape)) {
            Application.Quit();
        }
    }

    void OnApplicationQuit() {
        socket.Disconnect(true);
        socket.Close();
    }

    public void Send(byte data) {
        msg[0] = data;
        socket.Send(msg);
    }
}