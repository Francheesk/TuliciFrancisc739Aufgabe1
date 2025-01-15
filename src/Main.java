import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Spielort {
    private int Id;
    private String Team1;

    public Spielort(int id, String team1, String team2, Date datum, String spielort, int kapazitat) {
        Id = id;
        Team1 = team1;
        Team2 = team2;
        Datum = datum;
        Spielort = spielort;
        Kapazitat = kapazitat;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTeam1() {
        return Team1;
    }

    public void setTeam1(String team1) {
        Team1 = team1;
    }

    public String getTeam2() {
        return Team2;
    }

    public void setTeam2(String team2) {
        Team2 = team2;
    }

    public Date getDatum() {
        return Datum;
    }

    @Override
    public String toString() {
        return Team1 + " " + "vs " + Team2 + " " + "-" + " Datum: " + Datum.getYear() + "-" + Datum.getMonth() + "-" + Datum.getDay() + " " + "- " + "Spielort: " + Spielort + "\n";
    }

    public void setDatum(Date datum) {
        Datum = datum;
    }

    public String getSpielort() {
        return Spielort;
    }

    public void setSpielort(String spielort) {
        Spielort = spielort;
    }

    public int getKapazitat() {
        return Kapazitat;
    }

    public void setKapazitat(int kapazitat) {
        Kapazitat = kapazitat;
    }

    private String Team2;
    private Date Datum;
    private String Spielort;
    private int Kapazitat;
}

public class Main {

    public static List<Spielort> readPointsFile(String fileName) {
        List<Spielort> spielorts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Spielort currentSpielort = null;

            // Regex patterns to find tags
            Pattern idPattern = Pattern.compile("<Id>(\\d+)</Id>");
            Pattern datumPattern = Pattern.compile("<Datum>(.+?)</Datum>");
            Pattern team2Pattern = Pattern.compile("<Team2>(.+?)</Team2>");
            Pattern spielortPattern = Pattern.compile("<Spielort>(.+?)</Spielort>");
            Pattern kapazitatPattern = Pattern.compile("<Kapazität>(\\d+)</Kapazität>");
            Pattern team1Pattern = Pattern.compile("<Team1>(.+?)</Team1>");

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Check if the line contains a spielort block
                if (line.contains("<log>")) {
                    currentSpielort = new Spielort(0, " ", " ", new Date(), " ", 0);
                }

                if (currentSpielort != null) {
                    // Match each tag and extract the values
                    Matcher matcher;

                    // Extract spielort id
                    matcher = idPattern.matcher(line);
                    if (matcher.find()) {
                        currentSpielort.setId(Integer.parseInt(matcher.group(1)));
                    }

                    matcher = datumPattern.matcher(line);
                    if (matcher.find()) {
                        String[] dates = matcher.group(1).split("-");
                        currentSpielort.setDatum(new Date(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2])));
                    }

                    matcher = team2Pattern.matcher(line);
                    if (matcher.find()) {
                        currentSpielort.setTeam2(matcher.group(1));
                    }

                    matcher = team1Pattern.matcher(line);
                    if (matcher.find()) {
                        currentSpielort.setTeam1(matcher.group(1));
                    }

                    matcher = spielortPattern.matcher(line);
                    if (matcher.find()) {
                        currentSpielort.setSpielort(matcher.group(1));
                    }

                    matcher = kapazitatPattern.matcher(line);
                    if (matcher.find()) {
                        currentSpielort.setKapazitat(Integer.parseInt(matcher.group(1)));
                    }

                    // Once we have parsed all information for this spielort, add it to the list
                    if (line.contains("</log>")) {
                        spielorts.add(currentSpielort);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spielorts;
    }

    public static void displaySpielOrtByKapazitat(List<Spielort> spielorte, int wert) {
        List<Spielort> display = spielorte.stream()
                .filter(spiel -> spiel.getKapazitat() > wert)
                .collect(Collectors.toList());

        for(int i=0;i<display.size();i++) {
            System.out.print(display.get(i));
        }
    }

    public static void displaySpielOrtInMunchen(List<Spielort> spielorte) {
        List <Spielort> display = spielorte.stream()
                .filter(spiel -> Objects.equals(spiel.getSpielort(), "München"))
                .filter(spiel -> spiel.getDatum().after(new Date(2024,6,30)))
                .sorted((a,b) -> a.getDatum().compareTo(b.getDatum()))
                .collect(Collectors.toList());

        for(int i=0;i<display.size();i++) {
            System.out.print(display.get(i));
        }
    }

    public static void main(String[] args) {
        List<Spielort> spielorte = readPointsFile("spielorte.xml");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a number: ");

        int number = Integer.parseInt(scanner.nextLine());
        displaySpielOrtByKapazitat(spielorte, number);
        System.out.print("Games in munchen after the date given : \n");
        displaySpielOrtInMunchen(spielorte);
    }
}