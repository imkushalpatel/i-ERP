package helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.EditText;

public class ValidationMethod {

	// method for email validation
	public static boolean checkEmail(String email) {
		Pattern email_id = Pattern.compile(".+@.+\\.[a-z]+");
		return email_id.matcher(email).matches();
	}

	// method to check edittext is empty or not

	public static boolean checkEmpty(EditText etText) {
		if (etText.getText().toString().trim().trim().length() > 0)
			return true;
		else
			return false;
	}

	// method to convert password in md5 format

	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}