package com.android.PetPamper

import java.util.regex.Pattern

object StringSanitizer {
  fun sanitizeEmail(str: String): String {
    val cleanedEmail =
        str.replace(Regex("""[^a-zA-Z0-9\.\!\#\$\%\&\'\*\+\-\/\=\?\^\_\`\{\|\}\~\@]"""), "")
    val sanitizedEmail =
        cleanedEmail.replace(
            Regex("""(?<email>${RePatterns().AUTOLINK_EMAIL_ADDRESS.pattern()})(?<rest>.*)"""),
            { matchRes ->
              matchRes.groups["email"]?.value +
                  matchRes.groups["rest"]
                      ?.value
                      .toString()
                      .replace(Regex("""[^a-zA-Z0-9\.\-]"""), "")
            })
    return sanitizedEmail
  }

  fun sanitizePhone(str: String): String {
    val cleanedPhone =
        if (str.startsWith("+")) {
          str.replaceFirst("+", "00")
        } else {
          str
        }
    val sanitizedPhone = cleanedPhone.replace(Regex("""\D"""), "")
    return sanitizedPhone
  }

  fun sanitizeFloat(str: String): String {
    val cleanedNumber = str.replace(Regex("""[,'.]"""), ",").reversed().replaceFirst(",", ".")
    val sanitizedNumber = cleanedNumber.replace(Regex("""[^0-9,]"""), "")
    return sanitizedNumber
  }

  fun sanitizeInt(str: String): String {
    val cleanedNumber =
        str.replace(Regex("""[,'.]"""), ",").reversed().replaceFirst(",", ".").reversed()
    val sanitizedNumber = cleanedNumber.replace(Regex("""\D"""), "")
    return sanitizedNumber
  }
}

class RePatterns {
  public final val UCS_CHAR =
      "[" +
          "\u00A0-\uD7FF" +
          "\uF900-\uFDCF" +
          "\uFDF0-\uFFEF" +
          "\uD800\uDC00-\uD83F\uDFFD" +
          "\uD840\uDC00-\uD87F\uDFFD" +
          "\uD880\uDC00-\uD8BF\uDFFD" +
          "\uD8C0\uDC00-\uD8FF\uDFFD" +
          "\uD900\uDC00-\uD93F\uDFFD" +
          "\uD940\uDC00-\uD97F\uDFFD" +
          "\uD980\uDC00-\uD9BF\uDFFD" +
          "\uD9C0\uDC00-\uD9FF\uDFFD" +
          "\uDA00\uDC00-\uDA3F\uDFFD" +
          "\uDA40\uDC00-\uDA7F\uDFFD" +
          "\uDA80\uDC00-\uDABF\uDFFD" +
          "\uDAC0\uDC00-\uDAFF\uDFFD" +
          "\uDB00\uDC00-\uDB3F\uDFFD" +
          "\uDB44\uDC00-\uDB7F\uDFFD" +
          "&&[^\u00A0[\u2000-\u200A]\u2028\u2029\u202F\u3000]]"

  public final val LABEL_CHAR = "a-zA-Z0-9" + UCS_CHAR

  public final val TLD_CHAR = "a-zA-Z" + UCS_CHAR

  /** RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets. */
  public final val IRI_LABEL =
      "[" + LABEL_CHAR + "](?:[" + LABEL_CHAR + "\\-]{0,61}[" + LABEL_CHAR + "]){0,1}"

  /** RFC 3492 references RFC 1034 and limits Punycode algorithm output to 63 characters. */
  public final val PUNYCODE_TLD = "xn\\-\\-[\\w\\-]{0,58}\\w"

  public final val TLD = "(?:" + PUNYCODE_TLD + "|" + "[" + TLD_CHAR + "]{2,63}" + ")"
  // ?:

  public final val HOST_NAME = "(?:" + IRI_LABEL + "\\.)+" + TLD
  // ?:
  public final val WORD_BOUNDARY = "(?:\\b|$|^)"

  public final val EMAIL_CHAR =
      LABEL_CHAR + "\\.\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~"

  /**
   * Regular expression for local part of an email address. RFC5321 section 4.5.3.1.1 limits the
   * local part to be at most 64 octets.
   */
  public final val EMAIL_ADDRESS_LOCAL_PART =
      "[" + EMAIL_CHAR + "]" + "(?:[" + EMAIL_CHAR + "\\.]{0,62}[" + EMAIL_CHAR + "])?"

  /**
   * Regular expression for the domain part of an email address. RFC5321 section 4.5.3.1.2 limits
   * the domain to be at most 255 octets.
   */
  public final val EMAIL_ADDRESS_DOMAIN = "(?=.{1,255}(?:\\s|$|^))" + HOST_NAME

  /**
   * Regular expression pattern to match email addresses. It excludes double quoted local parts and
   * the special characters #&~!^`{}/=$*?| that are included in RFC5321.
   *
   * @hide
   */
  public final val AUTOLINK_EMAIL_ADDRESS: Pattern =
      Pattern.compile(
          "(?:" +
              WORD_BOUNDARY + // ?:
              "(?:" +
              EMAIL_ADDRESS_LOCAL_PART +
              "@" +
              EMAIL_ADDRESS_DOMAIN +
              ")" +
              WORD_BOUNDARY +
              ")")

  public final val EMAIL_ADDRESS: Pattern =
      Pattern.compile(
          "[a-zA-Z0-9\\.\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]{1,256}" +
              "\\@" +
              "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
              "(" +
              "\\." +
              "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
              ")+")
}
