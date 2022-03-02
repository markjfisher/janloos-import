# Jan Loos Import

Tools for importing and creating single VTT modules from Jan Loos patreon uploads.

we have our downloads.csv file with values:
```<url>|<name>```

e.g url is: https://www.patreon.com/file?h=1&i=2

## Running

### create downloads.csv
You need a file with all the download links somewhere to hand, of the format mentioned above, e.g

```text
https://www.patreon.com/file?h=1&i=2|Jan Loos Module Name 1
https://www.patreon.com/file?h=3&i=4|Jan Loos Module Name 2
```

The names are not important, just output while processing each line. The URL are from the patreon links provided
by Jan Loos in updates or emails.

### Get your patreon session_id

Once the downloads.csv file is in place, you need your session_id cookie from Patreon, for the application to be able
to download the patreon link file. This can be got from inspecting your cookies in a browser when logged into Patreon.

### Building and running the application

You will need java jdk 11 on your path as an absolute minimum. Gradle will do the rest for you.

Assuming linux, but windows should work using `gradle.bat` instead of `./gradlew`.

Build and show help for the application:
```bash
./gradlew :app:assemble
java -jar app/build/libs/app-DEV-all.jar dd -h
```

Run the application:

```bash
java -jar app/build/libs/app-DEV-all.jar dd -o /path/to/your/output/janloos-test -f /your/path/to/downloads.csv -i GB-your-session-id-here
```

Example run:

```text
20:54:15.367 INFO : Established active environments: [cli]
20:54:15.901 INFO : Creating JavaTimeModule for mn jackson mapper
20:54:17.393 INFO : Processing "Thugs and Thieves pack for Foundry VTT" with url "https://dl.dropbox.com/s/redacted/module.json?dl=1"
20:54:18.883 INFO : Getting archive for janloos-thugsandthieves
...
20:58:51.215 INFO : Processing "00 - Jan Loos Free Pack" with url "https://www.dropbox.com/s/more-redacted-paths/module.json?dl=1"
20:58:52.567 INFO : Getting archive for janloos-free-samples
20:58:57.469 INFO : Completed downloading all data to /path/to/your/output/janloos-test
```

## Archiving and uploading your zip file

This is a bit trickier.

For me, I can zip the directory created into a file, upload to my VTT server and unzip the file into the
modules directory, then after restarting Foundry VTT it appears in the module list under
```text
Jan Loos Combined Compendiums
```

I haven't tried any other mechanism as this is day 1 and I've only just written this.

Poke me in issues if you want more or have a better method.
