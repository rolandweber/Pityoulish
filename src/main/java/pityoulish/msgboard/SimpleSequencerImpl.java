/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;


/**
 * A simple, non-trivial implementation of {@link Sequencer}.
 * This implementation satisfies all of the MUST requirements,
 * and one of the SHOULD requirements. It rejects IDs from
 * other sequencer instances, with a certain probability.
 * <br>
 * The messages are internally numbered. The number is translated into
 * an alphabetic sequence instead of digits. A single-letter prefix,
 * chosen at random, distinguishes up to 26 different sequencers.
 */
public class SimpleSequencerImpl implements Sequencer
{
  /** The instance identifier. */
  protected final char instance;

  /** The counter for messages. */
  protected int counter;

  /** The pattern for checking sanity. */
  protected final static
    Pattern SANITY_PATTERN = Pattern.compile("[A-Z][a-z]+");


  /**
   * Creates a new instance.
   */
  public SimpleSequencerImpl()
  {
    instance = (char) ThreadLocalRandom.current().nextInt('A', 'Z'+1);

    // Numbers 0-25 can be encoded with a single character.
    // Initialize counter with a value close to the first overflow.
    //  0 ->  a
    //  1 ->  b
    // 25 ->  z
    // 26 -> ba
    counter = 22;
  }


  // non-javadoc, see interface
  public String createMessageID()
  {
    int num = counter++;

    StringBuilder sb = new StringBuilder(); // default length suffices

    // generate letters in reverse order
    do
     {
       sb.append((char)('a'+ (num % 26)));
       num = num / 26;
     }
    while (num > 0);
    sb.append(instance);

    sb.reverse();

    return sb.toString();
  }


  // non-javadoc, see interface
  public boolean isSane(String candidate)
  {
    // Message IDs start with the instance identifier and match the regex.

    return ((candidate != null) && (candidate.length() > 1) &&
            (candidate.charAt(0) == instance) &&
            SANITY_PATTERN.matcher(candidate).matches()
            );
  }


  // non-javadoc, see interface
  public Comparator<String> getComparator()
  {
    return SimpleComp.INSTANCE;
  }


  /** A comparator for the IDs generated by {@link SimpleSequencerImpl}. */
  public static class SimpleComp implements Comparator<String>
  {
    /**
     * The singleton instance of this comparator.
     * The implementation is thread-safe, there is no need to create another.
     */
    public final static SimpleComp INSTANCE = new SimpleComp();

    /** Restricted default constructor. */
    private SimpleComp()
    {
      // no body
    }


    // non-javadoc, see interface
    public int compare(String o1, String o2)
    {
      if (o1 == null)
         throw new NullPointerException("o1");
      if (o2 == null)
         throw new NullPointerException("o2");

      // If the arguments are not message IDs generated here, the result
      // does not matter. If they are, the longer string (more letters)
      // sorts after the shorter one. For strings with same length,
      // the alphabetical order corresponds to the numerical order.
      int result = o1.length() - o2.length();
      if (result == 0)
         result = o1.compareTo(o2);

      return result;
    }

  } // class SimpleComp

}

