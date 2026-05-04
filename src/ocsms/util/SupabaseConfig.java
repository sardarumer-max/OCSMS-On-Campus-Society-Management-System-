package ocsms.util;

public class SupabaseConfig {
    // ⚠️ PASTE YOUR SUPABASE PROJECT URL HERE
    public static final String SUPABASE_URL = "https://kqvunolcqgxfyajstfhd.supabase.co";
    
    // ⚠️ PASTE YOUR SUPABASE ANON (PUBLIC) KEY HERE
    public static final String SUPABASE_KEY = "sb_publishable_ot96YkhE2pv8EcyqQZif6w_u-Q7B93U";
    
    // Flag to check if credentials are set
    public static boolean isConfigured() {
        return !SUPABASE_URL.equals("YOUR_SUPABASE_URL_HERE") && 
               !SUPABASE_KEY.equals("YOUR_SUPABASE_ANON_KEY_HERE");
    }
}
