import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Player {
    public List<CardValue> cards;
    public List<String> suits; // only use for flush, straight flush and royal flush

    // Need to store the pair indexes as well
    int pairIndex = -1;
    int pair2Index = -1;
    int threeOfAKindIndex = -1;
    int fourOfAKindIndex = -1;

    public CardCombination combination;
    private boolean isFlush;
    private boolean isStraight;
    private boolean isFourOfAKind;
    private boolean isPair;
    private boolean isTwoPair;
    private boolean isThreeOfAKind;
    private boolean isFullHouse;
    private boolean isRoyalFlush;
    public int wins = 0;

    public Player() {
        cards = new ArrayList<>();
        suits = new ArrayList<>();
    }

    public CardCombination getCardCombination() {
        performCombinationLogic();
        return combination;
    }

    // resetting card properties
    public void resetCards() {
        cards.clear();
        suits.clear();
        combination = null;
        isFlush = false;
        isPair = false;
        isTwoPair = false;
        isThreeOfAKind = false;
        isStraight = false;
        isFourOfAKind = false;
        isFullHouse = false;
        isRoyalFlush = false;

        pairIndex = -1;
        pair2Index = -1;
        threeOfAKindIndex = -1;
        fourOfAKindIndex = -1;
    }

    private void performCombinationLogic() {

        sortCards();
        checkFlush();
        checkStraight();
        checkFourKind();
        checkThreeKind();
        checkTwoPair();
        checkPair();
        checkFullHouse();
        checkRoyalFlush();
        
        if(isRoyalFlush) {
            combination = CardCombination.ROYAL_FLUSH;
        }
        else if (isFlush && isStraight) {
            combination = CardCombination.STRAIGHT_FLUSH;
        }
        else if (isFourOfAKind) {
            combination = CardCombination.FOUR_OF_A_KIND;
        }
        else if (isFullHouse) {
            combination = CardCombination.FULL_HOUSE;
        }
        else if (isFlush) {
            combination = CardCombination.FLUSH;
        }
        else if (isStraight) {
            combination = CardCombination.STRAIGHT;
        }
        else if (isThreeOfAKind) {
            combination = CardCombination.THREE_OF_A_KIND;
        }
        else if (isTwoPair) {
            combination = CardCombination.TWO_PAIRS;
        }
        else if (isPair) {
            combination = CardCombination.PAIR;
        } else {
            combination = CardCombination.HIGH_CARD;
        }
    }

    private void sortCards() {
        Collections.sort(cards, new Comparator<CardValue>() {
            @Override
            public int compare(CardValue cv1, CardValue cv2) {
                return cv1.compareTo(cv2);
            }
        });
    }

    private void checkFullHouse() {
        isFullHouse = isPair && isThreeOfAKind;
    }

    private void checkRoyalFlush() {

        // the royal cards to check
        List<CardValue> royalSet = new ArrayList<>();
        royalSet.add(CardValue.ACE);
        royalSet.add(CardValue.KING);
        royalSet.add(CardValue.QUEEN);
        royalSet.add(CardValue.JACK);
        royalSet.add(CardValue.TEN);

        if(isFlush && isStraight && cards.containsAll(royalSet)) {
            isRoyalFlush = true;
        } else {
            isRoyalFlush = false;
        }
    }

    private boolean singlePairHelper(int x, int z) {
        if (threeOfAKindIndex != -1 && (cards.get(threeOfAKindIndex) == cards.get(x))) return false; // value used in three of a kind

        if (cards.get(x) == cards.get(z)) {
            pairIndex = x;
            isPair = true;
            return true;
        }
        return false; // not a pair
    }

    private void checkPair() {

        // We already have info if a pair exists
        if ((pairIndex != -1) || (pair2Index != -1)) {
            isPair = true;
            return;
        }

        // four cases, comparing with card next to each other
        if (singlePairHelper(0, 1)) return;
        if (singlePairHelper(1, 2)) return;
        if (singlePairHelper(2, 3)) return;
        if (singlePairHelper(3, 4)) return;
    }

    private boolean pairHelper(int c1, int c2) {
        if (pair2Index != -1 && pairIndex != -1) return false; // pairs exist
        if (threeOfAKindIndex != -1 && cards.get(threeOfAKindIndex) == cards.get(c1)) return false; // card is part of three of a kind
        if (pairIndex!= -1 && cards.get(pairIndex) == cards.get(c1)) return true; // pair already compared

        if(cards.get(c1) == cards.get(c2)) {
            if (pairIndex == -1) {
                pairIndex = c1;
            } else if (pair2Index == -1) {
                pair2Index = c1;
            }
            return true;
        }

        // not a pair
        return false;
    }

    private void checkTwoPair() {
        // since card is sorted we check two pair with three cases
        boolean case1 = pairHelper(0,1) && pairHelper(2,3);
        boolean case2 = pairHelper(0,1) && pairHelper(3,4);
        boolean case3 = pairHelper(1,2) && pairHelper(3,4);

        isTwoPair = case1 || case2 || case3;
    }

    private boolean threeOfAKindHelper(int x, int y, int z) {
        if (cards.get(x) == cards.get(y) &&
                cards.get(y) == cards.get(z)) return true;

        return false;
    }
    private void checkThreeKind() {

        /*
        CardValue firstCard = cards.get(0);
        CardValue secondCard = cards.get(1);
        CardValue thirdCard = cards.get(1);

        if (Collections.frequency(cards, firstCard) == 3 ||
                Collections.frequency(cards, secondCard) == 3 ||
                Collections.frequency(cards, thirdCard) == 3) {
            isThreeOfAKind = true;
        }*/
        if (threeOfAKindHelper(0, 1, 2)) threeOfAKindIndex = 0;
        if (threeOfAKindHelper(1, 2, 3)) threeOfAKindIndex = 1;
        if (threeOfAKindHelper(2, 3, 4)) threeOfAKindIndex = 2;

        isThreeOfAKind = threeOfAKindIndex != -1;
    }

    private void checkFourKind() {
        /*
        CardValue firstCard = cards.get(0);
        CardValue secondCard = cards.get(1);
        if (Collections.frequency(cards, firstCard) == 4 ||
            Collections.frequency(cards, secondCard) == 4) {
            isFourOfAKind = true;
        }
        */

        // since card is sorted only two cases, first card will be different or last card will be different
        boolean case1 = cards.get(0) == cards.get(1) &&
                cards.get(1) == cards.get(2) &&
                cards.get(2) == cards.get(3);

        boolean case2 = cards.get(1) == cards.get(2) &&
                cards.get(2) == cards.get(3) &&
                cards.get(3) == cards.get(4);

        if(case1) {
            fourOfAKindIndex = 0;
        } else if (case2) {
            fourOfAKindIndex = 4;
        }

        isFourOfAKind = case1 || case2;
    }

    private void checkStraight() {
        for (int i=0; i<4; i++) {
            if((cards.get(i).getAction() + 1) != cards.get(i+1).getAction()) {
                isStraight = false;
                return;
            }
        }
        isStraight = true;
    }

    private void checkFlush() {
        String firstSuit = suits.get(0);
        isFlush = suits.stream().allMatch(suit -> suit.equals(firstSuit));
    }

}
