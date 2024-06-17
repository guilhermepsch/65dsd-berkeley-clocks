public class MainDebug {

        public static void main(String[] args) {
            new Thread(() -> {
                Server.main(null);
            }).start();

            for (int i = 0; i < 4; i++) {
                new Thread(() -> {
                    Client.main(null);
                }).start();
            }
        }

}
