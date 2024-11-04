#!/bin/sh
gnome-terminal -- mvn spring-boot:run && xdg-open 'http://localhost:8080/home'
