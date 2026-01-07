package com.example.pomodorotimer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 番茄钟时间设置（单位：秒）
    private static final int WORK_TIME = 25 * 60;      // 25分钟工作时间
    private static final int SHORT_BREAK = 5 * 60;     // 5分钟短休息
    private static final int LONG_BREAK = 15 * 60;     // 15分钟长休息
    private static final int POMODOROS_UNTIL_LONG_BREAK = 4; // 长休息间隔

    private TextView timerTextView;
    private TextView statusTextView;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Button modeButton;
    private View circleView;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = WORK_TIME;
    private boolean timerRunning = false;
    private String currentMode = "work"; // work, shortBreak, longBreak
    private int completedPomodoros = 0;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        timerTextView = findViewById(R.id.timerTextView);
        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        modeButton = findViewById(R.id.modeButton);
        circleView = findViewById(R.id.circleView);

        preferences = getSharedPreferences("PomodoroTimer", MODE_PRIVATE);

        // 加载保存的状态
        loadState();

        // 设置按钮监听器
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());
        modeButton.setOnClickListener(v -> switchMode());

        updateTimerText();
        updateUI();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                updateCircleProgress();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                timeLeftInMillis = 0;
                updateTimerText();
                onTimerComplete();
            }
        }.start();

        timerRunning = true;
        updateUI();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        updateUI();
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;

        switch (currentMode) {
            case "work":
                timeLeftInMillis = WORK_TIME * 1000;
                break;
            case "shortBreak":
                timeLeftInMillis = SHORT_BREAK * 1000;
                break;
            case "longBreak":
                timeLeftInMillis = LONG_BREAK * 1000;
                break;
        }

        updateTimerText();
        updateCircleProgress();
        updateUI();
    }

    private void switchMode() {
        pauseTimer();

        if (currentMode.equals("work")) {
            currentMode = "shortBreak";
            timeLeftInMillis = SHORT_BREAK * 1000;
        } else if (currentMode.equals("shortBreak")) {
            currentMode = "longBreak";
            timeLeftInMillis = LONG_BREAK * 1000;
        } else {
            currentMode = "work";
            timeLeftInMillis = WORK_TIME * 1000;
        }

        updateTimerText();
        updateCircleProgress();
        updateUI();
    }

    private void onTimerComplete() {
        // 振动提醒
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(new long[]{0, 500, 200, 500}, -1);
        }

        // 播放提示音
        playNotificationSound();

        // 如果是工作模式完成，增加完成的番茄钟数量
        if (currentMode.equals("work")) {
            completedPomodoros++;
            saveState();

            // 自动切换到休息模式
            if (completedPomodoros % POMODOROS_UNTIL_LONG_BREAK == 0) {
                currentMode = "longBreak";
                timeLeftInMillis = LONG_BREAK * 1000;
            } else {
                currentMode = "shortBreak";
                timeLeftInMillis = SHORT_BREAK * 1000;
            }
        } else {
            // 休息结束，切换回工作模式
            currentMode = "work";
            timeLeftInMillis = WORK_TIME * 1000;
        }

        updateTimerText();
        updateCircleProgress();
        updateUI();
    }

    private void playNotificationSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void updateCircleProgress() {
        long totalTime;
        switch (currentMode) {
            case "work":
                totalTime = WORK_TIME * 1000;
                break;
            case "shortBreak":
                totalTime = SHORT_BREAK * 1000;
                break;
            default:
                totalTime = LONG_BREAK * 1000;
                break;
        }

        float progress = (float) timeLeftInMillis / totalTime;
        float angle = 360 * progress;

        // 使用圆形进度条动画
        ValueAnimator animator = ValueAnimator.ofFloat(circleView.getRotation(), angle);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            circleView.setRotation(animatedValue);
        });
        animator.start();
    }

    private void updateUI() {
        startButton.setEnabled(!timerRunning);
        pauseButton.setEnabled(timerRunning);

        // 更新状态文本
        String statusText;
        int backgroundColor;
        switch (currentMode) {
            case "work":
                statusText = "工作时间 - 专注！";
                backgroundColor = R.color.workColor;
                break;
            case "shortBreak":
                statusText = "短休息 - 放松一下";
                backgroundColor = R.color.shortBreakColor;
                break;
            default:
                statusText = "长休息 - 休息好再出发";
                backgroundColor = R.color.longBreakColor;
                break;
        }

        statusTextView.setText(statusText);
        circleView.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));

        // 显示完成的番茄钟数量
        if (completedPomodoros > 0) {
            statusTextView.setText(statusText + " (" + completedPomodoros + " 个番茄钟)");
        }
    }

    private void saveState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("completedPomodoros", completedPomodoros);
        editor.apply();
    }

    private void loadState() {
        completedPomodoros = preferences.getInt("completedPomodoros", 0);
        currentMode = preferences.getString("currentMode", "work");
        timeLeftInMillis = preferences.getLong("timeLeftInMillis", WORK_TIME * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // 自定义 CountDownTimer 类
    private static class CountDownTimer {
        private final long mMillisInFuture;
        private final long mCountdownInterval;
        private long mStopTimeInFuture;

        public CountDownTimer(long millisInFuture, long countDownInterval) {
            mMillisInFuture = millisInFuture;
            mCountdownInterval = countDownInterval;
        }

        public synchronized final void cancel() {
            mStopTimeInFuture = 0;
        }

        public synchronized final CountDownTimer start() {
            mStopTimeInFuture = System.currentTimeMillis() + mMillisInFuture;
            new Thread(() -> {
                while (System.currentTimeMillis() < mStopTimeInFuture) {
                    try {
                        Thread.sleep(mCountdownInterval);
                    } catch (InterruptedException e) {
                        return;
                    }
                    long millisUntilFinished = mStopTimeInFuture - System.currentTimeMillis();
                    onTick(millisUntilFinished);
                }
                onFinish();
            }).start();
            return this;
        }

        public void onTick(long millisUntilFinished) {}

        public void onFinish() {}
    }
}
