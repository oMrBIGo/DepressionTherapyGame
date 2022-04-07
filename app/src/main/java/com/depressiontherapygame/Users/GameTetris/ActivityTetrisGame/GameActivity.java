package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GestureDetectorCompat;

import com.depressiontherapygame.Users.GameTetris.base.AppBaseActivity;
import com.depressiontherapygame.Users.GameTetris.db.databasehelper.DatabaseHelper;
import com.depressiontherapygame.Users.GameTetris.db.tebles.GameLevel;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.GameTetris.utils.Consts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppBaseActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    final int BOARD_HEIGHT = 1000;
    final int BOARD_WIDTH = 500;
    final Handler handler = new Handler();
    final Shape[] shapes = new Shape[11];
    final int UP_DIRECTION = 0;
    final int RIGHT_DIRECTION = 1;
    final int DOWN_DIRECTION = 2;
    final int LEFT_DIRECTION = 3;
    final int dx[] = {-1, 0, 1, 0};
    final int dy[] = {0, 1, 0, -1};

    FirebaseAuth authProfile;

    @BindView(R.id.game_board)
    LinearLayout gameBoard;
    @BindView(R.id.LinearLayout1)
    LinearLayout LinearLayout1;
    @BindView(R.id.tv_level)
    AppCompatTextView tvLevel;
    @BindView(R.id.tv_score)
    AppCompatTextView tvScore;


    int NUM_ROWS = 0;
    int NUM_COLUMNS = 0;
    int SPEED_NORMAL = 500;
    int SPEED_FAST = 50;
    String difficulty, speed, level;

    int score;
    boolean gameInProgress, gamePaused, fastSpeedState, currentShapeAlive;

    Random random = new Random();
    BoardCell[][] gameMatrix;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    LinearLayout linearLayout;
    Shape currentShape;
    List<GameLevel> gameLevels = new ArrayList<>();
    private GestureDetectorCompat gestureDetector;
    private int selectedLevel;
    private Dialog mLevelUpDialog;
    private DatabaseHelper databaseHelper;
    private Vibrator vibrator;
    private int oldScore;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!gameInProgress) {
                return;
            }
            if (gamePaused) {
                PaintMatrix();
                if (fastSpeedState)
                    handler.postDelayed(this, SPEED_FAST);
                else
                    handler.postDelayed(this, SPEED_NORMAL);
                return;
            }

            boolean moved = MoveShape(DOWN_DIRECTION, currentShape);

            if (!moved) { // current shape is down
                // mark the new cells as fixed so they won't be affected by eventual bugs
                int ind, jnd, i, j;
                for (ind = 1, i = currentShape.x; ind <= 4; ++ind, ++i) {
                    for (jnd = 1, j = currentShape.y; jnd <= 4; ++jnd, ++j) {
                        if (currentShape.mat[ind][jnd].getState() == 1) {
                            gameMatrix[i][j].setBehavior(BoardCell.BEHAVIOR_IS_FIXED);
                            currentShape.mat[ind][jnd].setBehavior(BoardCell.BEHAVIOR_IS_FIXED);
                        }
                    }
                }
                currentShapeAlive = false;
                Check(); // check for complete rows
                currentShapeAlive = CreateShape(); // create another shape
                if (!currentShapeAlive) // if not possible, then game over
                {
                    gameInProgress = false;
                    PaintMatrix();
                    InsertScore();
                    return;
                }
                PaintMatrix();
                if (fastSpeedState) {
                    ChangeFastSpeedState(false);
                    return;
                }
            } else
                PaintMatrix();

            if (fastSpeedState)
                handler.postDelayed(this, SPEED_FAST);
            else
                handler.postDelayed(this, SPEED_NORMAL);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        AppCompatTextView button_exit = (AppCompatTextView) findViewById(R.id.exitBtn);
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });
        init();

        /* Initialize FirebaseAuth */
        authProfile = FirebaseAuth.getInstance();
    }

    private void init() {
        Intent intent = getIntent();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        databaseHelper = new DatabaseHelper(this);
        mLevelUpDialog = new Dialog(GameActivity.this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        gameLevels = databaseHelper.getAllScore();
        difficulty = prefs.getString("difficulty_preference", "Normal");
        NUM_ROWS = Integer.parseInt(prefs.getString("num_rows_preference", "24"));
        NUM_COLUMNS = Integer.parseInt(prefs.getString("num_columns_preference", "18"));
        selectedLevel = intent.getIntExtra("speed_preference", 1);

        switch (selectedLevel) {
            case 1:
                SPEED_NORMAL = 900;
                SPEED_FAST = 50;
                break;
            case 2:
                SPEED_NORMAL = 850;
                SPEED_FAST = 50;
                break;
            case 3:
                SPEED_NORMAL = 800;
                SPEED_FAST = 50;
                break;
            case 4:
                SPEED_NORMAL = 750;
                SPEED_FAST = 40;
                break;
            case 5:
                SPEED_NORMAL = 700;
                SPEED_FAST = 40;
                break;
            case 6:
                SPEED_NORMAL = 650;
                SPEED_FAST = 40;
                break;
            case 7:
                SPEED_NORMAL = 600;
                SPEED_FAST = 30;
                break;
            case 8:
                SPEED_NORMAL = 550;
                SPEED_FAST = 30;
                break;
            case 9:
                SPEED_NORMAL = 500;
                SPEED_FAST = 20;
                break;
            case 10:
                SPEED_NORMAL = 450;
                SPEED_FAST = 20;
                break;
            case 11:
                SPEED_NORMAL = 400;
                SPEED_FAST = 10;
                break;
            case 12:
                SPEED_NORMAL = 300;
                SPEED_FAST = 10;
                break;
            default:
                SPEED_NORMAL = 500;
                SPEED_FAST = 30;
                break;
        }
      /*  switch (speed) {
            case "Slow": {
                SPEED_NORMAL = 1000;
                SPEED_FAST = 50;
                break;
            }
            case "Normal": {
                SPEED_NORMAL = 500;
                SPEED_FAST = 30;
                break;
            }
            case "Fast": {
                SPEED_NORMAL = 250;
                SPEED_FAST = 10;
                break;
            }
        }*/
        score = 0;
        tvLevel.setText(String.valueOf(selectedLevel));
        tvScore.setText(String.valueOf(score));
        tvScore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(oldScore).equals(String.valueOf(score * 24))) {
                    oldScore = score * 24;
                    if (mPrefUtils.getBoolean(Consts.SharedPrefs.IS_VIBRATOR, true))
                        vibrator.vibrate(75);

                    if (mPrefUtils.getBoolean(Consts.SharedPrefs.IS_SOUND, true))
                        soundMove();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bitmap = Bitmap.createBitmap(BOARD_WIDTH, BOARD_HEIGHT, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        linearLayout = (LinearLayout) findViewById(R.id.game_board);

        currentShapeAlive = false;
        gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);

        ShapesInit();

        GameInit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gameInProgress) {
            gamePaused = true;
            PaintMatrix();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameInProgress) {
            gamePaused = false;
        }
    }

    private void ShapesInit() {
        int[][] a = new int[5][5];

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                a[i][j] = 0;
            }
        }

        // L
        a[1][2] = a[1][3] = a[2][3] = a[3][3] = 1;
        shapes[0] = new Shape(a, Color.rgb(255, 200, 100), BoardCell.BEHAVIOR_IS_FALLING);
        a[1][2] = a[1][3] = a[2][3] = a[3][3] = 0;

        // Z
        a[2][1] = a[2][2] = a[3][2] = a[3][3] = 1;
        shapes[1] = new Shape(a, Color.rgb(245, 65, 65), BoardCell.BEHAVIOR_IS_FALLING);
        a[2][1] = a[2][2] = a[3][2] = a[3][3] = 0;

        // I
        a[1][2] = a[2][2] = a[3][2] = a[4][2] = 1;
        shapes[2] = new Shape(a, Color.rgb(124, 246, 250), BoardCell.BEHAVIOR_IS_FALLING);
        a[1][2] = a[2][2] = a[3][2] = a[4][2] = 0;

        // O
        a[2][2] = a[2][3] = a[3][2] = a[3][3] = 1;
        shapes[3] = new Shape(a, Color.rgb(252, 252, 67), BoardCell.BEHAVIOR_IS_FALLING, false);
        a[2][2] = a[2][3] = a[3][2] = a[3][3] = 0;

        // T
        a[1][2] = a[2][2] = a[2][3] = a[3][2] = 1;
        shapes[4] = new Shape(a, Color.rgb(168, 64, 168), BoardCell.BEHAVIOR_IS_FALLING);
        a[1][2] = a[2][2] = a[2][3] = a[3][2] = 0;

        // S
        a[1][2] = a[2][2] = a[2][3] = a[3][3] = 1;
        shapes[5] = new Shape(a, Color.rgb(60, 159, 60), BoardCell.BEHAVIOR_IS_FALLING);
        a[1][2] = a[2][2] = a[2][3] = a[3][3] = 0;

        // J
        a[1][3] = a[2][3] = a[3][2] = a[3][3] = 1;
        shapes[6] = new Shape(a, Color.rgb(52, 143, 233), BoardCell.BEHAVIOR_IS_FALLING);
        a[1][3] = a[2][3] = a[3][2] = a[3][3] = 0;

        // hard mode

        // .
        a[2][2] = 1;
        shapes[7] = new Shape(a, Color.WHITE, BoardCell.BEHAVIOR_IS_FALLING, false);
        a[2][2] = 0;

        // +
        a[1][2] = a[2][1] = a[2][2] = a[2][3] = a[3][2] = 1;
        shapes[8] = new Shape(a, Color.GRAY, BoardCell.BEHAVIOR_IS_FALLING, false);
        a[1][2] = a[2][1] = a[2][2] = a[2][3] = a[3][2] = 0;

        // big cube
        for (int i = 1; i <= 4; ++i) for (int j = 1; j <= 4; ++j) a[i][j] = 1;
        shapes[9] = new Shape(a, Color.rgb(126, 83, 191), BoardCell.BEHAVIOR_IS_FALLING, false);
        for (int i = 1; i <= 4; ++i) for (int j = 1; j <= 4; ++j) a[i][j] = 0;

        //big H
        for (int i = 1; i <= 4; ++i) for (int j = 1; j <= 4; ++j) a[i][j] = 1;
        a[1][2] = a[1][3] = a[4][2] = a[4][3] = 0;
        shapes[10] = new Shape(a, Color.rgb(156, 205, 72), BoardCell.BEHAVIOR_IS_FALLING);
        for (int i = 1; i <= 4; ++i) for (int j = 1; j <= 4; ++j) a[i][j] = 0;
    }

    private void CopyMatrix(BoardCell[][] A, BoardCell[][] B) {
        for (int i = 0; i < NUM_ROWS; ++i) {
            for (int j = 0; j < NUM_COLUMNS; ++j) {
                B[i][j] = new BoardCell(A[i][j].getState(), A[i][j].getColor(), A[i][j].getBehavior());
            }
        }
    }

    private void FixGameMatrix() {
        for (int i = 3; i < NUM_ROWS - 3; ++i) {
            for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
                if (gameMatrix[i][j].getState() == 0) {
                    gameMatrix[i][j].setColor(R.color.box_color);
                    gameMatrix[i][j].setBehavior(BoardCell.BEHAVIOR_NOTHING);
                    continue;
                }
                if (gameMatrix[i][j].getBehavior() == BoardCell.BEHAVIOR_IS_FIXED)
                    continue;
                if (gameMatrix[i][j].getBehavior() == BoardCell.BEHAVIOR_IS_FALLING) {
                    int ind, jnd, ii, jj;
                    for (ind = 1, ii = currentShape.x; ind <= 4; ++ind, ++ii) {
                        for (jnd = 1, jj = currentShape.y; jnd <= 4; ++jnd, ++jj) {
                            if (ii == i && jj == j) {
                                if (currentShape.mat[ind][jnd].getState() == 0) {
                                    gameMatrix[i][j] = new BoardCell();
                                }
                            }
                        }
                    }
                    continue;
                }
                if (gameMatrix[i][j].getBehavior() == BoardCell.BEHAVIOR_NOTHING) {
                    int ind, jnd, ii, jj;
                    for (ind = 1, ii = currentShape.x; ind <= 4; ++ind, ++ii) {
                        for (jnd = 1, jj = currentShape.y; jnd <= 4; ++jnd, ++jj) {
                            if (ii == i && jj == j) {
                                if (currentShape.mat[ind][jnd].getState() == 1) {
                                    gameMatrix[i][j] = currentShape.mat[ind][jnd];
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean MoveShape(final int direction, Shape nowShape) {
        // copy the gameMatrix in aux
        BoardCell[][] aux = new BoardCell[NUM_ROWS][];
        for (int i = 0; i < NUM_ROWS; ++i)
            aux[i] = new BoardCell[NUM_COLUMNS];
        CopyMatrix(gameMatrix, aux);
        int i, ii, j, jj;
        // eliminate the shape from the table
        for (ii = nowShape.x, i = 1; i <= 4; ++i, ++ii) {
            for (jj = nowShape.y, j = 1; j <= 4; ++j, ++jj) {
                if (nowShape.mat[i][j].getState() == 1) {
                    gameMatrix[ii][jj] = new BoardCell();
                }
            }
        }

        // try to move the shape to the specified direction
        for (ii = nowShape.x + dx[direction], i = 1; i <= 4; ++i, ++ii) {
            for (jj = nowShape.y + dy[direction], j = 1; j <= 4; ++j, ++jj) {
                gameMatrix[ii][jj].setState(gameMatrix[ii][jj].getState() + nowShape.mat[i][j].getState());
                if (nowShape.mat[i][j].getState() == 1) {
                    gameMatrix[ii][jj].setColor(nowShape.mat[i][j].getColor());
                    gameMatrix[ii][jj].setBehavior(nowShape.mat[i][j].getBehavior());
                }
                if (gameMatrix[ii][jj].getState() > 1) {
                    CopyMatrix(aux, gameMatrix);
                    FixGameMatrix();
                    return false;
                }
            }
        }
        nowShape.x += dx[direction];
        nowShape.y += dy[direction];
        FixGameMatrix();
        return true;
    }

    private boolean RotateLeft(Shape nowShape) {
        // copy the gameMatrix in aux
        BoardCell[][] aux = new BoardCell[NUM_ROWS][];
        for (int i = 0; i < NUM_ROWS; ++i)
            aux[i] = new BoardCell[NUM_COLUMNS];
        CopyMatrix(gameMatrix, aux);
        int i, ii, j, jj;
        // eliminate the shape from the gameMatrix
        for (ii = nowShape.x, i = 1; i <= 4; ++i, ++ii) {
            for (jj = nowShape.y, j = 1; j <= 4; ++j, ++jj) {
                if (nowShape.mat[i][j].getState() == 1) {
                    gameMatrix[ii][jj] = new BoardCell();
                }
            }
        }
        // rotate the shape to left
        nowShape.RotateLeft();
        // ... and try to put it again on the table
        for (ii = nowShape.x, i = 1; i <= 4; ++i, ++ii) {
            for (jj = nowShape.y, j = 1; j <= 4; ++j, ++jj) {
                gameMatrix[ii][jj].setState(gameMatrix[ii][jj].getState() + nowShape.mat[i][j].getState());
                if (nowShape.mat[i][j].getState() == 1) {
                    gameMatrix[ii][jj].setColor(nowShape.mat[i][j].getColor());
                    gameMatrix[ii][jj].setBehavior(nowShape.mat[i][j].getBehavior());
                }
                // if we can't put the rotated shape on the table
                if (gameMatrix[ii][jj].getState() > 1) {
                    // then recreate the initial state of the table
                    CopyMatrix(aux, gameMatrix);
                    // ... and rotate the shape to right, to obtain its initial state
                    nowShape.RotateRight();
                    FixGameMatrix();
                    return false;
                }
            }
        }
        FixGameMatrix();
        return true;
    }

    private boolean RotateRight(Shape nowShape) {
        // copy the gameMatrix in aux
        BoardCell[][] aux = new BoardCell[NUM_ROWS][];
        for (int i = 0; i < NUM_ROWS; ++i)
            aux[i] = new BoardCell[NUM_COLUMNS];
        CopyMatrix(gameMatrix, aux);
        int i, ii, j, jj;
        // eliminate the shape from the gameMatrix
        for (ii = nowShape.x, i = 1; i <= 4; ++i, ++ii) {
            for (jj = nowShape.y, j = 1; j <= 4; ++j, ++jj) {
                if (nowShape.mat[i][j].getState() == 1) {
                    gameMatrix[ii][jj] = new BoardCell();
                }
            }
        }
        // rotate the shape to right
        nowShape.RotateRight();
        // ... and try to put it again on the table
        for (ii = nowShape.x, i = 1; i <= 4; ++i, ++ii) {
            for (jj = nowShape.y, j = 1; j <= 4; ++j, ++jj) {
                gameMatrix[ii][jj].setState(gameMatrix[ii][jj].getState() + nowShape.mat[i][j].getState());
                if (nowShape.mat[i][j].getState() == 1) {
                    gameMatrix[ii][jj].setColor(nowShape.mat[i][j].getColor());
                    gameMatrix[ii][jj].setBehavior(nowShape.mat[i][j].getBehavior());
                }
                // if we can't put the rotated shape on the table
                if (gameMatrix[ii][jj].getState() > 1) {
                    // then recreate the initial state of the table
                    CopyMatrix(aux, gameMatrix);
                    // ... and rotate the shape to left, to obtain its initial state
                    nowShape.RotateLeft();
                    FixGameMatrix();
                    return false;
                }
            }
        }
        FixGameMatrix();
        return true;
    }

    private boolean CreateShape() {
        // generate random shape to put on the gameMatrix

        if (difficulty.compareTo("Normal") == 0) {
            currentShape = shapes[random.nextInt(7)];
        } else {
            currentShape = shapes[random.nextInt(shapes.length)];
        }
        // generate random number of rotations
        int number_of_rotations = random.nextInt(4);
        for (int i = 1; i <= number_of_rotations; ++i) {
            currentShape.RotateRight();
        }
        currentShape.x = 0;
        currentShape.y = 1 + (NUM_COLUMNS - 6) / 2;
        // put the new generated shape adjacent to the top side of the table if possible
        for (int offset = 0; offset <= 3; ++offset) {
            int i, ii, j, jj;
            boolean ok = true;
            for (ii = currentShape.x + offset, i = 1; i <= 4; ++i, ++ii) {
                for (jj = currentShape.y, j = 1; j <= 4; ++j, ++jj) {
                    gameMatrix[ii][jj].setState(gameMatrix[ii][jj].getState() + currentShape.mat[i][j].getState());
                    if (gameMatrix[ii][jj].getState() > 1) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                for (i = 1, ii = currentShape.x + offset; i <= 4; ++i, ++ii) {
                    for (j = 1, jj = currentShape.y; j <= 4; ++j, ++jj) {
                        if (currentShape.mat[i][j].getState() == 1) {
                            gameMatrix[ii][jj].setColor(currentShape.mat[i][j].getColor());
                            gameMatrix[ii][jj].setBehavior(currentShape.mat[i][j].getBehavior());
                        }
                    }
                }
                currentShape.x += offset;
                FixGameMatrix();
                return true;
            } else {
                for (ii = currentShape.x + offset, i = 1; i <= 4; ++i, ++ii) {
                    for (jj = currentShape.y, j = 1; j <= 4; ++j, ++jj) {
                        gameMatrix[ii][jj].setState(gameMatrix[ii][jj].getState() - currentShape.mat[i][j].getState());
                    }
                }
            }
        }
        /// de avut grija
        FixGameMatrix();
        return false;
    }

    private boolean Check() {
        int k = 0;
        boolean found = false;
        for (int i = NUM_ROWS - 4; i >= 3; --i) {
            boolean ok = true;
            for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
                if (gameMatrix[i][j].getState() == 0) {
                    ok = false;
                }
            }
            if (ok) {
                ++k;
                found = true;
            } else {
                if (k == 0)
                    continue;
                for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
                    int state = gameMatrix[i][j].getState();
                    int color = gameMatrix[i][j].getColor();
                    int behavior = gameMatrix[i][j].getBehavior();
                    gameMatrix[i + k][j] = new BoardCell(state, color, behavior);
                }
            }
        }
        for (int pas = 0; pas < k; ++pas) {
            for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
                gameMatrix[3 + pas][j] = new BoardCell();
            }
        }
        // Update the score
        score += k * (k + 1) / 2;
        FixGameMatrix();
        return found;
    }

    void PaintMatrix() {

        // Paint the game board background
        paint.setColor(getResources().getColor(R.color.box_color));
        canvas.drawRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT, paint);

        // Paint the grid on the game board
        paint.setColor(getResources().getColor(R.color.grid_color));
        for (int i = 0; i <= (NUM_ROWS - 6); ++i) {
            canvas.drawLine(0, i * (BOARD_HEIGHT / (NUM_ROWS - 6)), BOARD_WIDTH,
                    i * (BOARD_HEIGHT / (NUM_ROWS - 6)), paint);
        }
        for (int i = 0; i <= (NUM_COLUMNS - 6); ++i) {
            canvas.drawLine(i * (BOARD_WIDTH / (NUM_COLUMNS - 6)), 0,
                    i * (BOARD_WIDTH / (NUM_COLUMNS - 6)), BOARD_HEIGHT, paint);
        }

        // Paint the tetris blocks
        for (int i = 3; i < NUM_ROWS - 3; ++i) {
            for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
                if (gameMatrix[i][j].getState() == 1) {
                    paint.setColor(gameMatrix[i][j].getColor());
                    canvas.drawRect((j - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            (j + 1 - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i + 1 - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            paint);
                }
            }
        }

        // Paint borders to the tetris blocks
        for (int i = 3; i < NUM_ROWS - 3; ++i) {
            for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
                if (gameMatrix[i][j].getState() == 1) {
                    paint.setColor(getResources().getColor(R.color.box_color));
                    canvas.drawLine((j - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            (j - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i + 1 - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            paint);
                    canvas.drawLine((j - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            (j + 1 - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            paint);
                    canvas.drawLine((j + 1 - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            (j + 1 - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i + 1 - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            paint);
                    canvas.drawLine((j - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i + 1 - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            (j + 1 - 3) * (BOARD_WIDTH / (NUM_COLUMNS - 6)),
                            (i + 1 - 3) * (BOARD_HEIGHT / (NUM_ROWS - 6)),
                            paint);
                }
            }
        }

        if (!gameInProgress) {
            int scoreOld = databaseHelper.getLevelStatusById(selectedLevel);
            if (scoreOld < (score * 24)) {
                databaseHelper.updatePuzzleScore(selectedLevel, score * 24);
            }
            showGameOverDialog();
        } else if (gamePaused) {
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(60);
            canvas.drawText("เกมหยุดชั่วคราว", (float) (BOARD_WIDTH / 2.0), (float) (BOARD_HEIGHT / 2.0), paint);
        }

        // Display the current painting
        linearLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));

        // Update the score textview

        tvScore.setText(String.valueOf(score * 24));


        if ((score * 24) > 0 && (score * 24) < 150) {
            if (selectedLevel < 1) {
                levelUp();
                selectedLevel = 1;
                gamePaused = true;
                SPEED_NORMAL = 1000;
                SPEED_FAST = 50;
                level = "เลเวล1";
                showLevelUpDialog();
            }
        } else if ((score * 24) > 150 && (score * 24) < 300) {
            if (selectedLevel < 2) {
                selectedLevel = 2;
                gamePaused = true;
                SPEED_NORMAL = 950;
                SPEED_FAST = 50;
                level = "เลเวล2";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 300 && (score * 24) < 450) {
            if (selectedLevel < 3) {
                selectedLevel = 3;
                gamePaused = true;
                SPEED_NORMAL = 900;
                SPEED_FAST = 50;
                level = "เลเวล3";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 450 && (score * 24) < 600) {
            if (selectedLevel < 4) {
                selectedLevel = 4;
                gamePaused = true;
                SPEED_NORMAL = 850;
                SPEED_FAST = 30;
                level = "เลเวล4";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 600 && (score * 24) < 750) {
            if (selectedLevel < 5) {
                selectedLevel = 5;
                gamePaused = true;
                SPEED_NORMAL = 800;
                SPEED_FAST = 30;
                level = "เลเวล5";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 750 && (score * 24) < 900) {
            if (selectedLevel < 6) {
                selectedLevel = 6;
                gamePaused = true;
                SPEED_NORMAL = 750;
                SPEED_FAST = 30;
                level = "เลเวล6";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 900 && (score * 24) < 1050) {
            if (selectedLevel < 7) {
                selectedLevel = 7;
                gamePaused = true;
                SPEED_NORMAL = 700;
                SPEED_FAST = 20;
                level = "เลเวล7";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 1050 && (score * 24) < 1200) {
            if (selectedLevel < 8) {
                selectedLevel = 8;
                gamePaused = true;
                SPEED_NORMAL = 650;
                SPEED_FAST = 20;
                level = "เลเวล8";
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 1200 && (score * 24) < 1350) {
            if (selectedLevel < 9) {
                selectedLevel = 9;
                gamePaused = true;
                SPEED_NORMAL = 600;
                SPEED_FAST = 20;
                level = "เลเวล9";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 1350 && (score * 24) < 1500) {
            if (selectedLevel < 10) {
                selectedLevel = 10;
                gamePaused = true;
                SPEED_NORMAL = 550;
                SPEED_FAST = 20;
                level = "เลเวล10";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 1500 && (score * 24) < 1650) {
            if (selectedLevel < 11) {
                selectedLevel = 11;
                gamePaused = true;
                SPEED_NORMAL = 500;
                SPEED_FAST = 20;
                level = "เลเวล11";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelUpDialog();
            }
        } else if ((score * 24) > 1650) {
            if (selectedLevel < 12) {
                selectedLevel = 12;
                gamePaused = true;
                SPEED_NORMAL = 450;
                SPEED_FAST = 20;
                level = "เลเวล12";
                tvLevel.setText(String.valueOf(selectedLevel));
                databaseHelper.updatePuzzleStatus(selectedLevel - 1, Consts.OtherConstant.LEVEL_COMPLETE, score * 24);
                if (gameLevels.get(selectedLevel - 1).getStatus().equals(Consts.OtherConstant.LEVEL_LOCK))
                    databaseHelper.updatePuzzleStatus(selectedLevel, Consts.OtherConstant.LEVEL_NOT_COMPLETE, 0);
                levelUp();
                showLevelEndDialog();
            }
        }
    }

    private void levelUp() {
        // Go to Score Activity
        final FirebaseUser firebaseUser = authProfile.getCurrentUser();

        HashMap<String, Object> result = new HashMap<>();
        result.put("level", level);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        String userID = firebaseUser.getUid();
        referenceProfile.child(userID).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }


    @Override
    public void onBackPressed() {
        showExitDialog();
    }


    /**
     * Show level up dialog
     */
    private void showLevelUpDialog() {

        if (mLevelUpDialog.getWindow() != null) {
            mLevelUpDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = mLevelUpDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            mLevelUpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mLevelUpDialog.setContentView(R.layout.dialog_level_completed);
            mLevelUpDialog.setCancelable(false);
            mLevelUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout llRestart = mLevelUpDialog.findViewById(R.id.ll_restart);
            AppCompatTextView tvLevel = mLevelUpDialog.findViewById(R.id.tv_level);
            AppCompatTextView tvLevelNext = mLevelUpDialog.findViewById(R.id.tv_next_level);
            tvLevel.setText(MessageFormat.format("เลเวล {0}", selectedLevel - 1));
            tvLevelNext.setText(MessageFormat.format("ไปด่านเลเวล {0}", selectedLevel));
            llRestart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLevelUpDialog.isShowing())
                        mLevelUpDialog.dismiss();

                    gamePaused = false;
                    //  onReplayClick();
                }
            });
            if (!mLevelUpDialog.isShowing())
                mLevelUpDialog.show();
        }
    }

    private void showLevelEndDialog() {

        gamePaused = true;
        Dialog mExitDialog = new Dialog(GameActivity.this);
        ((MusicPlayerActivity) GameActivity.this).doUnbindService();
        if (mExitDialog.getWindow() != null) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.victory);
            mp.start();
            mp.setLooping(false);
            mExitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = mExitDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            mExitDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mExitDialog.setContentView(R.layout.dialog_level_end);
            mExitDialog.setCancelable(false);
            mExitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llExit = mExitDialog.findViewById(R.id.ll_exit);

            llExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExitDialog.isShowing())
                        mExitDialog.dismiss();
                    mp.stop();
                    finish();
                }
            });

            mExitDialog.show();
        }
    }

    /**
     * Show Exit Dialog
     */
    private void showExitDialog() {
        gamePaused = true;
        Dialog mExitDialog = new Dialog(GameActivity.this);
        ((MusicPlayerActivity) GameActivity.this).doUnbindService();

        if (mExitDialog.getWindow() != null) {
            mExitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = mExitDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            mExitDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mExitDialog.setContentView(R.layout.dialog_exit);
            mExitDialog.setCancelable(false);
            mExitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llExit = mExitDialog.findViewById(R.id.ll_exit);
            LinearLayout llNo = mExitDialog.findViewById(R.id.ll_no);

            llNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExitDialog.isShowing())
                        mExitDialog.dismiss();
                    gamePaused = false;
                }
            });

            llExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExitDialog.isShowing())
                        mExitDialog.dismiss();

                    finish();
                }
            });

            mExitDialog.show();
        }
    }

    /**
     * Show Game Over Dialog
     */
    private void showGameOverDialog() {

        Dialog mGameOverDialog = new Dialog(GameActivity.this);

        if (mGameOverDialog.getWindow() != null) {
            mGameOverDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = mGameOverDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            mGameOverDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mGameOverDialog.setContentView(R.layout.dialog_game_over);
            mGameOverDialog.setCancelable(false);
            mGameOverDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout llRestart = mGameOverDialog.findViewById(R.id.ll_restart);
            LinearLayout llHome = mGameOverDialog.findViewById(R.id.ll_home);

            llRestart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGameOverDialog.isShowing())
                        mGameOverDialog.dismiss();
                    init();
                    //  onReplayClick();
                }
            });
            llHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGameOverDialog.isShowing())
                        mGameOverDialog.dismiss();
                    finish();
                    //  onReplayClick();
                }
            });
            mGameOverDialog.show();
        }
    }

    private void InsertScore() {
   /*     ContentValues values = new ContentValues();
        values.put(HighScoresDb.KEY_NAME, MainActivity.playerName);
        values.put(HighScoresDb.KEY_SCORE, Integer.toString(score));
        values.put(HighScoresDb.KEY_DIFFICULTY, difficulty);
        values.put(HighScoresDb.KEY_NUMROWS, Integer.toString(NUM_ROWS - 6));
        values.put(HighScoresDb.KEY_NUMCOLUMNS, Integer.toString(NUM_COLUMNS - 6));
        values.put(HighScoresDb.KEY_SPEED, speed);

        Uri uri = getContentResolver().insert(HighScoresContentProvider.CONTENT_URI, values);*/
    }

    void ChangeFastSpeedState(boolean mFastSpeedState) {
        // fastSpeedState = false for normal speed
        // fastSpeedState = true for fast speed
        handler.removeCallbacks(runnable);
        fastSpeedState = mFastSpeedState;
        if (fastSpeedState)
            handler.postDelayed(runnable, SPEED_FAST);
        else
            handler.postDelayed(runnable, SPEED_NORMAL);
    }

    void GameInit() {

        // Create the game board (backend)
        gameMatrix = new BoardCell[NUM_ROWS][];
        for (int i = 0; i < NUM_ROWS; ++i) {
            gameMatrix[i] = new BoardCell[NUM_COLUMNS];
            for (int j = 0; j < NUM_COLUMNS; ++j) {
                gameMatrix[i][j] = new BoardCell();
            }
        }

        // Marking the first and the last 3 lines and columns as occupied.

        for (int j = 0; j < NUM_COLUMNS; ++j) {
            for (int i = 0; i <= 2; ++i) {
                gameMatrix[i][j] = new BoardCell(1, R.color.box_color);
            }
            for (int i = NUM_ROWS - 3; i < NUM_ROWS; ++i) {
                gameMatrix[i][j] = new BoardCell(1, R.color.box_color);
            }
        }

        for (int i = 0; i < NUM_ROWS; ++i) {
            for (int j = 0; j <= 2; ++j) {
                gameMatrix[i][j] = new BoardCell(1, R.color.box_color);
            }
            for (int j = NUM_COLUMNS - 3; j < NUM_COLUMNS; ++j) {
                gameMatrix[i][j] = new BoardCell(1, R.color.box_color);
            }
        }

        for (int j = 3; j < NUM_COLUMNS - 3; ++j) {
            gameMatrix[NUM_ROWS - 4][j] = new BoardCell(gameMatrix[NUM_ROWS - 4][j].getState(), gameMatrix[NUM_ROWS - 4][j].getColor(), BoardCell.BEHAVIOR_IS_FIXED);
        }


        // Create an initial tetris block
        currentShapeAlive = CreateShape();

        // Start the game
        gameInProgress = true;
        gamePaused = false;

        // Paint the initial matrix (frontend)
        PaintMatrix();

        // Set a timer
        ChangeFastSpeedState(false);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (gamePaused || !currentShapeAlive)
            return false;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        float x = e.getX();
        if (x <= width / 2.0) {
            // rotate left
            RotateLeft(currentShape);
            PaintMatrix();
        } else {
            // rotate right
            RotateRight(currentShape);
            PaintMatrix();
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!gameInProgress)
            return false;
        try {
            float x1 = e1.getX();
            float y1 = e1.getY();

            float x2 = e2.getX();
            float y2 = e2.getY();

            double angle = getAngle(x1, y1, x2, y2);

            if (inRange(angle, 45, 135)) {
                // UP
                // pause
             /*   if (gamePaused)
                    gamePaused = false;
                else {
                    gamePaused = true;
                    PaintMatrix();
                }*/
            } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                // RIGHT
                // move right
                if (gamePaused || !currentShapeAlive)
                    return false;
                MoveShape(RIGHT_DIRECTION, currentShape);
                PaintMatrix();
            } else if (inRange(angle, 225, 315)) {
                // DOWN
                // move fast down
                if (gamePaused || !currentShapeAlive)
                    return false;
                ChangeFastSpeedState(true);
            } else {
                // LEFT
                // move left
                if (gamePaused || !currentShapeAlive)
                    return false;
                MoveShape(LEFT_DIRECTION, currentShape);
                PaintMatrix();
            }

        } catch (Exception e) {
            // nothing
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

    private boolean inRange(double angle, float init, float end) {
        return (angle >= init) && (angle < end);
    }

    @Override
    protected void onDestroy() {
        vibrator.cancel();
        super.onDestroy();
    }

    @OnClick({R.id.tv_score})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_score:
                break;
        }
    }

    public class BoardCell {
        public final static int BEHAVIOR_IS_FIXED = 2, BEHAVIOR_IS_FALLING = 1, BEHAVIOR_NOTHING = 0;
        private int state, color, behavior;
        // state = 0 means free cell, state = 1 means occupied cell

        public BoardCell() {
            state = 0;
            color = R.color.box_color;
            behavior = BEHAVIOR_NOTHING;
        }

        public BoardCell(int state, int color) {
            this.state = state;
            this.color = color;
            this.behavior = BEHAVIOR_NOTHING;
        }

        public BoardCell(int state, int color, int behavior) {
            this.state = state;
            this.color = color;
            this.behavior = behavior;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getBehavior() {
            return behavior;
        }

        public void setBehavior(int behavior) {
            this.behavior = behavior;
        }
    }

    public class Shape {
        public int x, y;
        public BoardCell[][] mat = new BoardCell[5][5];
        public boolean canRotate;

        Shape() {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    mat[i][j] = new BoardCell();
                }
            }
            x = y = 0;
            canRotate = true;
        }

        Shape(int[][] _mat, int _color) {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (_mat[i][j] == 1) {
                        mat[i][j] = new BoardCell(_mat[i][j], _color);
                    } else {
                        mat[i][j] = new BoardCell();
                    }
                }
            }
            x = y = 0;
            canRotate = true;
        }

        Shape(int[][] _mat, int _color, final int behavior) {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (_mat[i][j] == 1)
                        mat[i][j] = new BoardCell(_mat[i][j], _color, behavior);
                    else
                        mat[i][j] = new BoardCell();

                }
            }
            canRotate = true;
        }

        Shape(int[][] _mat, int _color, final int behavior, boolean _canRotate) {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (_mat[i][j] == 1)
                        mat[i][j] = new BoardCell(_mat[i][j], _color, behavior);
                    else
                        mat[i][j] = new BoardCell();

                }
            }
            canRotate = _canRotate;
        }

        Shape(int[][] _mat, int _color, int _x, int _y) {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (_mat[i][j] == 1) {
                        mat[i][j] = new BoardCell(_mat[i][j], _color);
                    } else {
                        mat[i][j] = new BoardCell();
                    }
                }
            }
            x = _x;
            y = _y;
            canRotate = true;
        }


        void RotateLeft() {
            if (!this.canRotate) {
                return;
            }

            BoardCell[][] aux = new BoardCell[5][5];
            for (int i = 1; i < 5; ++i) {
                for (int j = 1; j < 5; ++j) {
                    aux[4 - j + 1][i] = mat[i][j];
                }
            }
            for (int i = 1; i < 5; ++i) {
                for (int j = 1; j < 5; ++j) {
                    mat[i][j] = aux[i][j];
                }
            }
        }

        void RotateRight() {
            if (!this.canRotate) {
                return;
            }

            BoardCell[][] aux = new BoardCell[5][5];
            for (int i = 1; i < 5; ++i) {
                for (int j = 1; j < 5; ++j) {
                    aux[j][4 - i + 1] = mat[i][j];
                }
            }
            for (int i = 1; i < 5; ++i) {
                for (int j = 1; j < 5; ++j) {
                    mat[i][j] = aux[i][j];
                }
            }
        }
    }
}
