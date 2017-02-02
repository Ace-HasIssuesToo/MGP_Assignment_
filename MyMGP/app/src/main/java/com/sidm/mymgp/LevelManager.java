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
    // Enemy waypoints
    Vector<Vector2D> m_des;
    // Determines which pattern player is going to dish out
    Enemy.PATTERN m_fingerpattern = Enemy.PATTERN.TYPE_MAX_TYPE;
    public int[] m_fingerindex; // Guess you can say this game is best played with the index_finger, badumtss.
    public int INDEX;
    public int score_multiplier; // Constant to calculate score multiplied -> calculated using total number of enemies killed * score_multiplier

    public LevelManager()
    {
        m_numNode = 4;
        INDEX = 0;
        m_enemySpeed = 200.0f;
        m_enemyStartPos = Vector2D.Zero;
        m_numEnemies = 0;
        m_des = new Vector<>(); // Vector of waypoints
        score_multiplier = 1;
    }

    public void Init(int nodes_, Vector2D enemy_start_, int enemy_num_, float enemy_speed_)
    {
        m_numNode = nodes_;
        m_enemyStartPos = enemy_start_;
        m_numEnemies = enemy_num_;
        m_enemySpawnIndex = enemy_num_ - 1;
        m_enemySpeed = enemy_speed_;
    }
}
