using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Button : MonoBehaviour {
    [SerializeField] private int index = 1;
    public int touchIndex = -1;
    private bool initial = false, toggle = false;
    private Vector3 olddetla, posdelta, oldscale;
    private byte mask = 1;
    private ButtonController controller;
    private SpriteRenderer box;

    void Start() {
        controller = GetComponentInParent<ButtonController>();
        box = GetComponent<SpriteRenderer>();
        mask = (byte) (1 << (index - 1));
    }

    void Update() {
        if (touchIndex != -1) {
            controller.buffer |= mask;
            box.color = Color.red;
            Touch temptouch = Input.GetTouch(touchIndex);
            RaycastHit2D hit = Physics2D.Raycast(Camera.main.ScreenToWorldPoint(temptouch.position), transform.forward, 15f);
            if (hit.transform == transform) {
                if (temptouch.phase == TouchPhase.Ended) Ended();
            } else Ended();
        } else box.color = Color.green;

        if (toggle) Change();
    }

    private void Ended() {
        touchIndex = -1;
        toggle = false;
        initial = false;
    }

    public void Toggle() {
        if (touchIndex != -1) toggle = !toggle;
    }

    private void Change() {
        if (!initial) {
            ChangeInitail();
            initial = true;
        }
        box.color = Color.blue;
        Vector3 newpos0 = Camera.main.ScreenToWorldPoint(Input.GetTouch(0).position);
        Vector3 newpos1 = Camera.main.ScreenToWorldPoint(Input.GetTouch(1).position);
        Vector3 newdelta = newpos0 - newpos1;
        transform.localScale = new Vector3(oldscale.x + Mathf.Abs(newdelta.x) - Mathf.Abs(olddetla.x), oldscale.y + Mathf.Abs(newdelta.y) - Mathf.Abs(olddetla.y), 0);
        transform.position = new Vector3((newpos0.x + newpos1.x) / 2 + posdelta.x, (newpos0.y + newpos1.y) / 2 + posdelta.y, 0);
    }

    private void ChangeInitail() {
        Vector3 oldpos0 = Camera.main.ScreenToWorldPoint(Input.GetTouch(0).position);
        Vector3 oldpos1 = Camera.main.ScreenToWorldPoint(Input.GetTouch(1).position);
        olddetla = oldpos0 - oldpos1;
        posdelta = transform.position - (oldpos0 + oldpos1) / 2;;
        oldscale = transform.localScale;
    }
}