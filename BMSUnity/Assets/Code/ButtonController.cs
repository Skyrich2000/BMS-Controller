using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using UnityEngine;

public class ButtonController : MonoBehaviour {
    [SerializeField] private GameObject network;
    public byte buffer = 0;
    private RaycastHit2D hit;
    private Touch temptouch;

    void Update() {
        try {
            Send();
            Touch();
        } catch (Exception e) {
            Debug.Log("Socket send or receive error ! : " + e.ToString());
        }
    }

    void Send() {        
        network.GetComponent<BluetoothControl>().Send(buffer);
        buffer = 0;
    }

    void Touch() {
        for (int i = 0; i < Input.touchCount; i++) {
            temptouch = Input.GetTouch(i);
            hit = Physics2D.Raycast(Camera.main.ScreenToWorldPoint(temptouch.position), transform.forward, 15f);
            if (hit.collider != null) {
                hit.transform.GetComponent<Button>().touchIndex = i;
            }
        }
    }
}