import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static Map<String, CardValue> cardValueMap = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("\tPOKER HAND SORTER");
        System.out.println("----------------------------------");
        loadMap(cardValueMap);

        try {
            File myFile = new File(args[0]);
            Scanner myReader = new Scanner(myFile);

            // Initialize players
            Player player1 = new Player();
            Player player2 = new Player();

            while(myReader.hasNextLine()) {

                String[] data = myReader.nextLine().split(" ");
                // load the cards into players
                loadCardsToPlayers(player1, player2, data);

                // Check the players card combination
                int p1CombinationValue = player1.getCardCombination().getAction();
                int p2CombinationValue = player2.getCardCombination().getAction();

                if(p1CombinationValue > p2CombinationValue) {
                    player1.wins += 1;
                } else if(p1CombinationValue < p2CombinationValue) {
                    player2.wins += 1;
                } else {
                    // Used for comparing pair values
                    int p1Index;
                    int p2Index;
                    boolean pairsOutcome = false;

                    // Compare four of a kind
                    if(player1.combination == CardCombination.FOUR_OF_A_KIND) {
                        // compare the indexes to determine which is higher
                        p1Index = player1.fourOfAKindIndex;
                        p2Index = player2.fourOfAKindIndex;

                        if (hasPairsOutcomeDecided(player1, p1Index, player2, p2Index)) pairsOutcome = true;
                    }

                    // Compare three of a kind
                    if (player1.combination == CardCombination.THREE_OF_A_KIND) {
                        p1Index = player1.threeOfAKindIndex;
                        p2Index = player2.threeOfAKindIndex;
                        if (hasPairsOutcomeDecided(player1, p1Index, player2, p2Index)) pairsOutcome = true;
                    }

                    // Compare two pairs
                    if (player1.combination == CardCombination.TWO_PAIRS) {
                        p1Index = player1.pair2Index;
                        p2Index = player2.pair2Index;
                        if (hasPairsOutcomeDecided(player1, p1Index, player2, p2Index)) pairsOutcome = true;

                        // compare the second pair
                        if (!pairsOutcome) {
                            p1Index = player1.pairIndex;
                            p2Index = player2.pairIndex;
                            if (hasPairsOutcomeDecided(player1, p1Index, player2, p2Index)) pairsOutcome = true;
                        }
                    }

                    // Compare a Pair
                    if (player1.combination == CardCombination.PAIR) {
                        p1Index = player1.pairIndex;
                        p2Index = player2.pairIndex;
                        if (hasPairsOutcomeDecided(player1, p1Index, player2, p2Index)) pairsOutcome = true;
                    }

                    // Compare their hands if still draw
                    if (!pairsOutcome) {
                        for (int i = 4; i >= 0; i--) {
                            if (player1.cards.get(i).getAction() > player2.cards.get(i).getAction()) {
                                player1.wins += 1;
                                break;
                            } else if (player1.cards.get(i).getAction() < player2.cards.get(i).getAction()) {
                                player2.wins += 1;
                                break;
                            }
                        }
                    }
                }

                // reset cards
                player1.resetCards();
                player2.resetCards();
            }

            // Outputting the wins
            System.out.println("\tPlayer 1: " + player1.wins + " hands.");
            System.out.println("\tPlayer 2: " + player2.wins + " hands.");

        }
        catch (FileNotFoundException ex) {
            System.out.println("An error occurred.");
            ex.printStackTrace();
        }
    }

    private static void loadCardsToPlayers(Player player1, Player player2, String[] data) {
        for (int i = 0; i < data.length; i++) {
            String card = String.valueOf(data[i].substring(0,1));
            String suit = String.valueOf(data[i].substring(1));

            if (i < 5) {
                player1.cards.add(cardValueMap.get(card));
                player1.suits.add(suit);
            } else {
                player2.cards.add(cardValueMap.get(card));
                player2.suits.add(suit);
            }
        }
    }

    private static boolean hasPairsOutcomeDecided(Player p1, int p1Index, Player p2, int p2Index) {
        if(p1.cards.get(p1Index).getAction() > p2.cards.get(p2Index).getAction()) {
            p1.wins += 1;
            return true;
        }
        else if (p1.cards.get(p1Index).getAction() < p2.cards.get(p2Index).getAction()) {
            p2.wins += 1;
            return true;
        }

        return false;
    }

    private static void loadMap(Map<String,CardValue> cardValueMap) {
        cardValueMap.put("2",CardValue.TWO);
        cardValueMap.put("3",CardValue.THREE);
        cardValueMap.put("4",CardValue.FOUR);
        cardValueMap.put("5",CardValue.FIVE);
        cardValueMap.put("6",CardValue.SIX);
        cardValueMap.put("7",CardValue.SEVEN);
        cardValueMap.put("8",CardValue.EIGHT);
        cardValueMap.put("9",CardValue.NINE);
        cardValueMap.put("T",CardValue.TEN);
        cardValueMap.put("J",CardValue.JACK);
        cardValueMap.put("Q",CardValue.QUEEN);
        cardValueMap.put("K",CardValue.KING);
        cardValueMap.put("A",CardValue.ACE);
    }
}
