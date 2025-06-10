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
				"___  ___                 _                       ______ _           _____  _                 _   \n" +
				"|  \\/  |                (_)                     |___  /(_)         /  ___|| |               | |  \n" +
				"| .  . | _   _  _ __     _   __ _  _ __    __ _    / /  _  _ __    \\ `--. | |_   __ _  _ __ | |_ \n" +
				"| |\\/| || | | || '_ \\   | | / _` || '_ \\  / _` |  / /  | || '_ \\    `--. \\| __| / _` || '__|| __|\n" +
				"| |  | || |_| || | | |  | || (_| || | | || (_| |./ /___| || |_) |  /\\__/ /| |_ | (_| || |   | |_ \n" +
				"\\_|  |_/ \\__,_||_| |_|  | | \\__,_||_| |_| \\__, |\\_____/|_|| .__/   \\____/  \\__| \\__,_||_|    \\__|\n" +
				"                       _/ |                __/ |          | |                                    \n" +
				"                      |__/                |___/           |_|                                    \n");
	}

}
