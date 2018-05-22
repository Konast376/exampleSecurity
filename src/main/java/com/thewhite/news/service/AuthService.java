package com.thewhite.news.service;

import com.thewhite.news.model.UserDetails;

import java.util.UUID;

/**
 * Created by Seredkin M. on 11.12.2017.
 * <p>
 * Сервис для работы с авторизацией
 *
 * @author Maxim Seredkin
 * @version 1.0.0
 */
public interface AuthService {
    /**
     * Получить текущего пользователя
     *
     * @return текущий пользователь
     */
     UserDetails getCurrentUser();

    /**
     * Получить идентификатор текущего пользователя
     *
     * @return идентификатор текущего пользователя
     */
    UUID getCurrentUserId();
}
