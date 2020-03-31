using UnityEngine;
using UnityEngine.UI;

public class BluetoothControl : MonoBehaviour {

    public Text uitext, inputtext;
    private bool isConnected = false;
    AndroidJavaObject mblue;

    void Start() {
        //SerialPortControl.GetInstance().OpenPort("COM3", 9600, System.IO.Ports.Parity.None, 8, System.IO.Ports.StopBits.One);
        //AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        //_activity = jc.GetStatic<AndroidJavaObject>("currentActivity");
        mblue = new AndroidJavaObject("com.huni.bluetooth.BluetoothService");
        if (mblue.Call<bool>("CheckBluetoothAvailable")) {
            Log("Bluetooth is available");
            Log(mblue.Call<string>("SearchDevice"));
        } else Log("Bluetooth is not available");
    }

    void Update() {
        if (Input.GetKeyDown(KeyCode.Escape)) {
            Application.Quit();
        }
    }

    public void ConnectedDevice() {
        string str;
        uitext.text = "";
        Log("connect start : " + inputtext.text);
        str = mblue.Call<string>("ConnectedDevice", inputtext.text);
        Log(str);
        if (str == "connect success!") isConnected = true;
    }

    public void Send(byte data) {
        //SerialPortControl.GetInstance().SendData(str);
        string log;
        if (isConnected) {
            log = mblue.Call<string>("Send", data);
            if (log != "") Log(log);
        }
    }

    void OnApplicationQuit() {
        mblue.Call("closeSocket");
    }
    /*
    public void LogMessage(string msg) {
        Log("LogMessage :: " + msg);
    }

    public void ConnectOn(string str) {
        text.GetComponent<Text>().text = "";
    }

    public void ReceiveData(string str) {
        Log("Received :: " + str);
    }
    */
    private void Log(string str) {
        uitext.text += str + (char) 10;
    }
}