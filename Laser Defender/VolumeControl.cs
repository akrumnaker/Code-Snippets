using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.SceneManagement;

public class VolumeControl : MonoBehaviour {

    public Slider musicSlider;
    public Slider soundSlider;

    private bool loading = true;

    void Awake()
    {
        // Set the sliders to the appropriate levels based on saved data
        musicSlider.value = DataLoader.loader.GetMusicVolume();
        soundSlider.value = DataLoader.loader.GetSoundVolume();

        // Set the toggles for music and sound based on saved data
        Toggle musicToggle = GameObject.Find("Music Toggle").GetComponent<Toggle>();
        musicToggle.isOn = DataLoader.loader.GetMusicOn();
        Toggle soundToggle = GameObject.Find("Sound Toggle").GetComponent<Toggle>();
        soundToggle.isOn = DataLoader.loader.GetSoundOn();

        // Delegate that will save the volume settings to a file
        SceneManager.sceneLoaded += OnSceneLoaded;
        loading = false;
    }

    public void MusicVolume()
    {
        DataLoader.loader.SetMusicVolume(musicSlider.value);
        MusicPlayer.instance.SetMusicVolume(DataLoader.loader.GetMusicVolume());
    }

    public void MusicOn()
    {
        // Prevent the OnValueChanged Function being called when scene has just loaded
        if (!loading)
        {
            DataLoader.loader.SetMusicOn();
        }
        MusicPlayer.instance.SetMusicOn(DataLoader.loader.GetMusicOn());
    }

    public void SoundVolume()
    {
        DataLoader.loader.SetSoundVolume(soundSlider.value);
    }

    public void SoundOn()
    {
        // Prevent the OnValueChanged Function being called when scene has just loaded
        if (!loading)
        {
            DataLoader.loader.SetSoundOn();
        }
    }

    void OnSceneLoaded(Scene scene, LoadSceneMode loadSceneMode)
    {
        // Save the volume settings that the player set when the go back to the main menu
        if (scene.buildIndex == 0)
        {
            DataLoader.loader.SaveVolumeSettings();
        }
    }

}
