using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class PlayerManager : MonoBehaviour {

    public static PlayerManager manager;
    public int lives = 3;
    public GameObject player;
    public Text livesCounter;

    void Start()
    {
        if(manager != this)
        {
            manager = this;
        }
    }

    public void AddLife()
    {
        // Add a life and adjust livesCounter text
        lives++;
        livesCounter.text = "X" + lives;
    }

    public void RespawnPlayer()
    {
        lives--;
        livesCounter.text = "X" + lives;
        // Load the Win Screen if the player has no lives left
        if (lives <= 0)
        {
            LevelManager lvlMngr = GameObject.Find("LevelManager").GetComponent<LevelManager>();
            lvlMngr.LoadLevel("Win Screen");
        }else
        {
            //Instantiate new instance of player object
            Instantiate(player, transform.position, Quaternion.identity);
        }
    }
}
