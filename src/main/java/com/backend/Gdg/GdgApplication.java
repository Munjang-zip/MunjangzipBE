package com.backend.Gdg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GdgApplication {

	public static void main(String[] args) {
		SpringApplication.run(GdgApplication.class, args);
		System.out.println("[Initiate GDG Project]");
		System.out.println("\n" +
				"___  ___                 _                       ______ _        \n" +
				"|  \\/  |                (_)                     |___  /(_)       \n" +
				"| .  . | _   _  _ __     _   __ _  _ __    __ _    / /  _  _ __  \n" +
				"| |\\/| || | | || '_ \\   | | / _` || '_ \\  / _` |  / /  | || '_ \\ \n" +
				"| |  | || |_| || | | |  | || (_| || | | || (_| |./ /___| || |_) |\n" +
				"\\_|  |_/ \\__,_||_| |_|  | | \\__,_||_| |_| \\__, |\\_____/|_|| .__/ \n" +
				"                       _/ |                __/ |          | |    \n" +
				"                      |__/                |___/           |_|    ");
		System.out.println("\n" +
				"      _                 _   \n" +
				"     | |               | |  \n" +
				" ___ | |_   __ _  _ __ | |_ \n" +
				"/ __|| __| / _` || '__|| __|\n" +
				"\\__ \\| |_ | (_| || |   | |_ \n" +
				"|___/ \\__| \\__,_||_|    \\__|\n" +
				"                            \n" +
				"                            \n");
	}

}
