package com.engteam14.yorkpirates;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import java.util.Random;


public class SoundManager{
	private Array<Sound> snds_cannon_shoot;
	private Array<Sound> snds_hurt;
	private Sound snd_game_win;
	private Sound snd_game_lose;
	private Sound snd_death;
	private Sound snd_menu_button;
	private float volume;
	private Random randGen;
	
	/**
	 * SoundManager class. Has method calls for all the sounds the game should make.
	 * */
	public SoundManager() {
		
		//Set volume (0 by default)
		volume = 0;
		
		//Set sound files
		snds_cannon_shoot = new Array<Sound>();
		snds_hurt = new Array<Sound>();
		snd_game_win = Gdx.audio.newSound(Gdx.files.internal("sfx_Game_Win.wav"));
		snd_game_lose = Gdx.audio.newSound(Gdx.files.internal("sfx_Game_Lose.wav"));
		snd_death = Gdx.audio.newSound(Gdx.files.internal("sfx_College_Death.wav"));
		snd_menu_button = Gdx.audio.newSound(Gdx.files.internal("sfx_Button.wav"));
		
		//Set multiple sound files to the same sound bank for variation in sounds which
		//will play frequently in the game
		snds_cannon_shoot.add(	Gdx.audio.newSound(Gdx.files.internal("sfx_Cannon_Shoot2.wav")),
								Gdx.audio.newSound(Gdx.files.internal("sfx_Cannon_Shoot3.wav"))	);
		
		snds_hurt.add(	Gdx.audio.newSound(Gdx.files.internal("sfx_Hurt1.wav")),
						Gdx.audio.newSound(Gdx.files.internal("sfx_Hurt2.wav")),
						Gdx.audio.newSound(Gdx.files.internal("sfx_Hurt3.wav"))	);
		
		//Random generator for randomly chosen sounds
		randGen = new Random();
	}
	
	public void setVolume(float vol) {
		this.volume = vol;
	}
	
	public void cannon() {
		snds_cannon_shoot.get(randGen.nextInt(2)).play(this.volume);
	}
	
	public void death() {
		snd_death.play(this.volume);
	}
	
	public void damage() {
		snds_hurt.get(randGen.nextInt(3)).play(this.volume);
	}
	
	public void menu_button() {
		snd_menu_button.play(this.volume);
	}
	
	public void win() {
		snd_game_win.play(this.volume);
	}
	
	public void lose() {
		snd_game_lose.play(this.volume);
	}
}