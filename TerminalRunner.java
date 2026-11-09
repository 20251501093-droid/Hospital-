public class TerminalRunner {
    
    // Load the C++ Dynamic Library
    static {
        System.loadLibrary("BillingBridge");
    }

    public static void main(String[] args) {
        System.out.println("=== IIIT VADODARA BACKEND TERMINAL TEST ===");
        
        try {
            // Trigger the C++ Virtual Base Classes & Pointers directly
            String receipt = BillingBridge.processDischarge("B1001", 1500.00, "Platinum Lounge");
            
            System.out.println(receipt);
            System.out.println("\n[System] C++ Bridge executed successfully in terminal mode.");
            
        } catch (Exception e) {
            System.out.println("Native Bridge Error: " + e.getMessage());
        }
    }
}