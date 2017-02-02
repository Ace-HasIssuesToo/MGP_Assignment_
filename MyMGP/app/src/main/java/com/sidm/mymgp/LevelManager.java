package com.sidm.mymgp;

import java.util.Vector;

/**
 * Created by User on 1/2/2017.
 */

public class LevelManager {
    public int m_numNode; // Number of nodes in the level
// computer pos
    // Create a list of enemies, use a EnemyManager class is needed in future
    public int m_numEnemies;
    // For index spawning of enemy
    public int m_enemySpawnIndex;
    public float m_enemySpeed;
    public Vector2D m_enemyStartPos;
    /*allies*/
    public int m_numAlly;
    // For index spawning of enemy
    public int m_allySpawnIndex;
    public float m_allySpeed;
    public Vector2D m_allyStartPos;
    /**/
    // Enemy waypoints
    Vector<Vector2D> m_des;
    // Determines which pattern player is going to dish out
    Enemy.PATTERN m_fingerpattern = Enemy.PATTERN.TYPE_MAX_TYPE;
    public int[] m_fingerindex; // Guess you can say this game is best played with the index_finger, badumtss.
    public int INDEX;
    public int score_multiplier; // Constant to calculate score multiplied -> calculated using total number of enemies killed * score_multiplier

    // The list of LevelManager instances, each representing one level. Use shared preferences to pass selected level number from LevelPage to GamePlay page
    public static Vector<LevelManager> m_levellist = new Vector<>(10, 2);;

    public LevelManager()
    {
        m_numNode = 4;
        INDEX = 0;
        m_enemySpeed = 200.0f;
        m_enemyStartPos = Vector2D.Zero;
        m_numEnemies = 0;
        m_allySpeed = 200.0f;
        m_allyStartPos = Vector2D.Zero;
        m_numAlly = 0;
        m_des = new Vector<>(); // Vector of waypoints
        score_multiplier = 1;
    }

    public void Init(int nodes_, Vector2D enemy_start_, int enemy_num_, float enemy_speed_, Vector2D ally_start_, int ally_num_, float ally_speed_)
    {
        m_numNode = nodes_;
        m_enemyStartPos = enemy_start_;
        m_numEnemies = enemy_num_;
        m_enemySpawnIndex = enemy_num_ - 1;
        m_enemySpeed = enemy_speed_;
        m_allyStartPos = ally_start_;
        m_numAlly = ally_num_;
        m_allySpawnIndex = ally_num_ - 1;
        m_allySpeed = ally_speed_;
    }

    public void SetEnemiesDestination(int currentlevelindex, float Screenwidth, float Screenheight, Vector2D com_pos)
    {
        int index = currentlevelindex - 1;
        switch (index)
        {
            case 0:
                m_levellist.elementAt(index).m_des.add(new Vector2D(Screenwidth * 0.4f, Screenheight * 0.6f));
                m_levellist.elementAt(index).m_des.add(new Vector2D(Screenwidth * 0.4f, com_pos.y));
                m_levellist.elementAt(index).m_des.add(com_pos);
                break;
            case 1:
                m_levellist.elementAt(index).m_des.add(m_enemyStartPos);
                m_levellist.elementAt(index).m_des.add(new Vector2D(Screenwidth * 0.5f, com_pos.y));
                m_levellist.elementAt(index).m_des.add(com_pos);
                break;
        }
    }
}
