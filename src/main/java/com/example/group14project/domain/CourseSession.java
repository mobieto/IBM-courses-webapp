package com.example.group14project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
public class CourseSession {


    //works for now, should change to autogenerated int
    @Id
    private String courseName;
    private LocalDateTime startTime;

    private LocalDateTime endGoal;

    private String endGoalName;

    private Long pauseTime;
    private Duration pausedDuration;
    private LocalDateTime endTime;
    private String totalTime;

    public CourseSession() {
        this.pausedDuration = Duration.ZERO;
    }

    public CourseSession(String courseName, LocalDateTime startTime) {
        this.courseName = courseName;
        this.startTime = startTime;
        this.pauseTime = null;
        this.pausedDuration = Duration.ZERO;
    }

    public void start() {
        this.startTime = LocalDateTime.now();
    }

    public void pause() {
        if (pauseTime == null) {
            pauseTime = System.currentTimeMillis();
            System.out.println("Session paused at: " + Instant.ofEpochMilli(pauseTime));
        } else {
            System.out.println("Session is already paused.");
        }
    }

    public void resume() {
        if (pauseTime != null) {
            long resumeTime = System.currentTimeMillis();
            System.out.println("Session resumed at: " + Instant.ofEpochMilli(resumeTime));
            pausedDuration = pausedDuration.plusMillis(resumeTime - pauseTime);
            pauseTime = null;
        }
    }

    public Duration getElapsedTime() {
        if (pauseTime != null) {
            LocalDateTime pauseDateTime = Instant.ofEpochMilli(pauseTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
            return Duration.between(startTime, pauseDateTime).plus(pausedDuration);
        } else {
            return Duration.between(startTime, LocalDateTime.now()).minus(pausedDuration);
        }
    }

    public String getFormattedElapsedTime() {
        Duration elapsedTime = calculateElapsedTime();

        long hours = elapsedTime.toHours();
        long minutes = elapsedTime.toMinutesPart();
        long seconds = elapsedTime.toSecondsPart();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private Duration calculateElapsedTime() {
        if (pauseTime != null) {
            LocalDateTime pauseDateTime = Instant.ofEpochMilli(pauseTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
            return Duration.between(startTime, pauseDateTime).plus(pausedDuration);
        } else {
            return Duration.between(startTime, LocalDateTime.now()).minus(pausedDuration);
        }
    }

    public void stop() {
        if (startTime != null && pauseTime == null) {
            endTime = LocalDateTime.now();
            Duration elapsedTime = calculateElapsedTime();

            long hours = elapsedTime.toHours();
            long minutes = elapsedTime.toMinutesPart();
            long seconds = elapsedTime.toSecondsPart();

            totalTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            System.out.println("Session stopped at: " + endTime);
        } else {
            System.out.println("Session cannot be stopped at this time.");
        }
    }

    public String getTotalTime() {
        return totalTime;
    }

    public boolean isSessionStarted() {
        return startTime != null;
    }

    public boolean isSessionPaused() {
        return pauseTime != null;
    }

    public void reset() {
        startTime = null;
        pauseTime = null;
        pausedDuration = Duration.ZERO;
        endTime = null;
    }

    public String getCourseName() {
        return courseName;
    }

    public LocalDateTime getEndGoal() {
        return endGoal;
    }

    public void setEndGoal(LocalDateTime endGoal, String goalName) {
        if(this.endGoal == null) {
            this.endGoal = endGoal;
            this.endGoalName = goalName;
            return;
        }

        if(endGoal.isBefore(this.endGoal)) {
            this.endGoal = endGoal;
            this.endGoalName = goalName;
        }

    }

    public String getFormattedendGoal() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
        return this.endGoal.format(formatter);
    }

    public Badge getBadge() {
        String badgeName = courseName + " " + endGoalName + " badge";
        String badgeDescription = "awarded for finishing this course in record time!";
        return new Badge(badgeName, badgeDescription);
    }
}