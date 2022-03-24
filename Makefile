eb:
	rm -f build/libs/*
	./gradlew bootJar
	cp build/libs/*.jar application.jar
	zip app.zip application.jar -r .ebextensions/** -r .platform/** Procfile
	rm application.jar
	eb deploy --staged


clean:
	rm -f build/libs/*
	rm app.zip