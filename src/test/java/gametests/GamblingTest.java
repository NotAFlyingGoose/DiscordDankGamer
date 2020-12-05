package gametests;

public class GamblingTest {

    public static void main(String[] args) {
        System.out.println("Gambling Addiction is a serious problem, if you or anyone you know has a problem find help here: https://www.addictioncenter.com/drugs/gambling-addiction/. That being said, I hope you enjoy my game about gambling!");
        Deck deck = Deck.createStandardDeck().shuffle();

        Hand yourHand = new Hand(deck);
        yourHand.cards.add(new Card(Suit.WILD, 0));
        yourHand.cards.add(new Card(Suit.SPADES, 2));
        yourHand.cards.add(new Card(Suit.CLUBS, 1));
        yourHand.cards.add(new Card(Suit.DIAMONDS, 1));
        yourHand.cards.add(new Card(Suit.HEARTS, 1));

        System.out.println(yourHand.cards);
        System.out.println(yourHand.getScore());
    }

}
