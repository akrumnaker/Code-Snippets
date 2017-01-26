using UnityEngine;
using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Collections;

public class DataLoader : MonoBehaviour {

    public static DataLoader loader;

    private int highScore = 0;
    private float musicVolume = 0.5f;
    private float soundVolume = 0.0f;
    private bool musicOn = true;
    private bool soundOn = false;

    private String dataPath;

    void Awake()
    {
        // Singleton Design Pattern to insure only one DataLoader will exist at a given time
        if (loader == null)
        {
            // Get the data path that will be used for the save file
            dataPath = Application.persistentDataPath + "/playerInfo.dat";
            DontDestroyOnLoad(this.gameObject);
            loader = this;
            
            // Load the data from the save file
            Load();
            
            MusicPlayer.instance.SetMusicVolume(musicVolume);
            MusicPlayer.instance.SetMusicOn(musicOn);
        }
        else if (loader != this)
        {
            Destroy(gameObject);
        }
    }

    public void SetMusicVolume(float musicVolume)
    {
        loader.musicVolume = musicVolume;
    }

    public float GetMusicVolume()
    {
        return loader.musicVolume;
    }

    public void SetSoundVolume(float soundVolume)
    {
        loader.soundVolume = soundVolume;
    }

    public float GetSoundVolume()
    {
        return loader.soundVolume;
    }

    public void SetMusicOn()
    {
        loader.musicOn = !loader.musicOn;
    }

    public bool GetMusicOn()
    {
        return loader.musicOn;
    }

    public void SetSoundOn()
    {
        loader.soundOn = !loader.soundOn;
    }

    public bool GetSoundOn()
    {
        return loader.soundOn;
    }

    public int GetHighScore()
    {
        return loader.highScore;
    }

    public void SaveVolumeSettings()
    {
        // Create BinaryFormatter to encrypt the save file
        BinaryFormatter bf = new BinaryFormatter();
        // Create or Open the file based off of the path in dataPath
        FileStream file = File.Create(dataPath);

        // Initialize the variables that will be stored in the file
        PlayerData data = new PlayerData();
        data.musicVolume = loader.musicVolume;
        data.soundVolume = loader.soundVolume;
        data.musicOn = loader.musicOn;
        data.soundOn = loader.soundOn;
        data.highScore = loader.highScore;

        // Encrypt the data into the file
        bf.Serialize(file, data);
        // Close the file
        file.Close();
    }

    public void SaveHighScore(int highScore)
    {
        loader.highScore = highScore;
        //if player's score > high score, store as new highscore
        // Create BinaryFormatter to encrypt the save file
        BinaryFormatter bf = new BinaryFormatter();
        // Create or Open the file based off of the path in dataPath
        FileStream file = File.Create(dataPath);

        // Initialize the variables that will be stored in the file
        PlayerData data = new PlayerData();
        data.musicVolume = loader.musicVolume;
        data.soundVolume = loader.soundVolume;
        data.musicOn = loader.musicOn;
        data.soundOn = loader.soundOn;
        data.highScore = loader.highScore;

        // Encrypt the data into the file
        bf.Serialize(file, data);
        // Close the file
        file.Close();
    }

    public void Load()
    {
        // Load the file only if the file exists
        if (File.Exists(dataPath))
        {
            // Create BinaryFormatter to decrypt the save file
            BinaryFormatter bf = new BinaryFormatter();
            // Open the file for reading
            FileStream file = File.Open(dataPath, FileMode.Open);

            // Decrypt the file for reading and store the data into a PlayerData object
            PlayerData data = (PlayerData)bf.Deserialize(file);
            // Close the file
            file.Close();

            // Assign the variables of the loader object
            loader.musicVolume = data.musicVolume;
            loader.soundVolume = data.soundVolume;
            loader.musicOn = data.musicOn;
            loader.soundOn = data.soundOn;
            loader.highScore = data.highScore;
        }
    }

}

[Serializable]
class PlayerData
{
    public float musicVolume;
    public float soundVolume;
    public bool musicOn;
    public bool soundOn;
    public int highScore;
}

