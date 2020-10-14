package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {
    // класс проверки авторизации
    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) { // конструктор Объекта юзер
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<UserData> users; // Сюда входят юзеры

    public SimpleAuthService() {
        users = new ArrayList<>(); // создаем список пользователей
        users.add(new UserData("qwe", "qwe", "qwe"));
        users.add(new UserData("asd", "asd", "asd"));
        users.add(new UserData("zxc", "zxc", "zxc"));
        for (int i = 1; i < 10; i++) {
            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    // проверка на правильность ввода пользователем логина и пароля
    public String getNicknameByLoginAndPassword(String login, String password) {

        for (UserData user : users) {
            if(user.login.equals(login) && user.password.equals(password)){
                return user.nickname; // если все гуд выдай ник пользователю
            }
        }
        return null; // если нет нул присвой
    }
}
