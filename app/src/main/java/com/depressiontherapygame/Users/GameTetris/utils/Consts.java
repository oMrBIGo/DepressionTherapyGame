package com.depressiontherapygame.Users.GameTetris.utils;

public class Consts {

    public interface SharedPrefs {
        String IS_SOUND = "is_sound";
        String IS_MUSIC = "is_music";
        String IS_VIBRATOR = "is_vibrator";
    }

    /**
     * other constant must be put here
     */
    public interface OtherConstant {
        String IS_FIRST_TIME = "is_first_time";

        String LEVEL_COMPLETE = "complete";
        String LEVEL_NOT_COMPLETE = "not_complete";
        String LEVEL_LOCK = "locked";

        String PRIVACY_URL = "https://firebasestorage.googleapis.com/v0/b/agile-projects-6b727.appspot.com/o/Bricks%20Puzzle%2Fprivacy_policy.html?alt=media&token=48680fb7-c15d-45c8-b423-84e049f72262";
        String TERMS_AND_CONDITION_URL = "https://firebasestorage.googleapis.com/v0/b/agile-projects-6b727.appspot.com/o/Bricks%20Puzzle%2Fterms_and_conditions.html?alt=media&token=1de012a2-6af9-4406-a3d0-80a51aa4d389";

    }

    /**
     * all common formats
     */
    public interface CommonFormat {
    }


    /**
     * all keys for bundle/intent extras must be put here
     */
    public interface BundleExtras {

    }

    /**
     * all type of request codes must put here
     */
    public interface RequestCode {
    }

    /**
     * all date time formats must be put here
     */
    public interface DateTimeFormats {

    }

    /**
     * all time delays must be put here
     */
    public interface Delays {
    }
}