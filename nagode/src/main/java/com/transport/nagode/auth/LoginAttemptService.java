package com.transport.nagode.auth;


import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000; // 15 minutes

    private final ConcurrentMap<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        LoginAttempt attempt = attemptsCache.getOrDefault(key, new LoginAttempt());
        attempt.incrementAttempts();
        attempt.setLastAttempt(System.currentTimeMillis());
        attemptsCache.put(key, attempt);
    }

    public boolean isBlocked(String key) {
        LoginAttempt attempt = attemptsCache.get(key);
        if (attempt == null) {
            return false;
        }

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            if (System.currentTimeMillis() - attempt.getLastAttempt() < LOCK_TIME_DURATION) {
                return true;
            } else {
                // Réinitialiser après la période de verrouillage
                attemptsCache.remove(key);
                return false;
            }
        }
        return false;
    }

    public void cleanAttempts(String key) {
        attemptsCache.remove(key);
    }

    private static class LoginAttempt {
        private int attempts;
        private long lastAttempt;

        public void incrementAttempts() {
            this.attempts++;
        }

        public int getAttempts() { return attempts; }
        public long getLastAttempt() { return lastAttempt; }
        public void setLastAttempt(long lastAttempt) { this.lastAttempt = lastAttempt; }
    }
}
