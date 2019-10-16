package me.justinlane.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;

import java.util.stream.Stream;

/**
 * The brands class stores brand suffixes, directories and includes methods for
 * retrieving that info.
 * 
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class Brands {

    final private HashMap<String, String[]> BRAND_SUFFIX = new HashMap<String, String[]>();
    final private HashMap<String, Path> BRAND_DIRS = new HashMap<String, Path>();

    /** 
     * This constructor adds the appropriate keys and their values to 
     * the BRAND_SUFFIX and BRAND_DIRS hashmaps.
     */
    public Brands() {
      String[] ace = new String[]{"ACE", "GWY", "EMA"};
      String[] app = new String[]{"APP"};
      String[] asu = new String[]{"ASU"};
      String[] del = new String[]{"DEL", "DRC"};
      String[] hew = new String[]{"HEW"};
      String[] len = new String[]{"LEN"};
      String[] sac = new String[]{"SAC"};
      String[] syc = new String[]{"SYC"};
      String[] tsc = new String[]{"TSC"};
      String[] iby = new String[]{"IBY"};
      String[] msi = new String[]{"MSS"};
      String[] cbp = new String[]{"CBP"};
      String[] rzr = new String[]{"RZR"};
      String[] all = Stream.of(ace, app, asu, del, hew, len, sac, syc, tsc, iby, msi, cbp, rzr)
                           .flatMap(Stream::of)
                           .toArray(String[]::new);

      this.BRAND_SUFFIX.put("Acer", ace);
      this.BRAND_SUFFIX.put("Apple", app);
      this.BRAND_SUFFIX.put("Asus", asu);
      this.BRAND_SUFFIX.put("Dell/Alienware", del);
      this.BRAND_SUFFIX.put("Hewlett Packard", hew);
      this.BRAND_SUFFIX.put("Lenovo", len);
      this.BRAND_SUFFIX.put("Samsung", sac);
      this.BRAND_SUFFIX.put("Sony", syc);
      this.BRAND_SUFFIX.put("Toshiba", tsc);
      this.BRAND_SUFFIX.put("iBuyPower", iby);
      this.BRAND_SUFFIX.put("MSI", msi);
      this.BRAND_SUFFIX.put("CyberPowerPC", cbp);
      this.BRAND_SUFFIX.put("Razer", rzr);
      this.BRAND_SUFFIX.put("Canada", all);
      this.BRAND_SUFFIX.put("All", all);
            
      String baseDir = "//VSP021320/GSC-Pub/BOM Squad/BOM-Smart Parts";
      this.BRAND_DIRS.put("Acer", Paths.get(baseDir, "Acer_Gateway_Emachine"));
      this.BRAND_DIRS.put("Apple", Paths.get(baseDir, "Apple-APP"));
      this.BRAND_DIRS.put("Asus", Paths.get(baseDir, "Asus-ASU"));
      this.BRAND_DIRS.put("Dell/Alienware", Paths.get(baseDir, "Dell_Dell Reclamation"));
      this.BRAND_DIRS.put("Hewlett Packard", Paths.get(baseDir, "HP_Compaq"));
      this.BRAND_DIRS.put("Lenovo", Paths.get(baseDir, "Lenovo-LEN"));
      this.BRAND_DIRS.put("Samsung", Paths.get(baseDir, "Samsung Computer-SAC"));
      this.BRAND_DIRS.put("Sony", Paths.get(baseDir, "Sony Computer-SYC"));
      this.BRAND_DIRS.put("Toshiba", Paths.get(baseDir, "Toshiba Computer-TSC"));
      this.BRAND_DIRS.put("iBuyPower", Paths.get(baseDir, "iBuyPower-IBY"));
      this.BRAND_DIRS.put("MSI", Paths.get(baseDir, "MSI-MSS"));
      this.BRAND_DIRS.put("CyberPowerPC", Paths.get(baseDir, "CyberPowerPC-CBP"));
      this.BRAND_DIRS.put("Razer", Paths.get(baseDir, "Razer-RZR"));
      this.BRAND_DIRS.put("Canada", Paths.get(baseDir, "Canada", "Computers"));
      this.BRAND_DIRS.put("All", Paths.get(baseDir));
      
    }
    
    /**
     * Returns BRAND_SUFFIX hashmap.
     */
    public HashMap<String, String[]> getBrands() {
      return this.BRAND_SUFFIX;
    }
    
    /**
     * Returns BRAND_DIRS hashmap.
     */
    public HashMap<String, Path> getBrandDirs() {
      return this.BRAND_DIRS;
    }
    
    /**
     * Returns full brand names.
     */
    public String[] getBrandsLong() {
      return this.BRAND_SUFFIX.keySet().toArray(new String[this.BRAND_SUFFIX.size()]);
    }
    
    /**
     * Returns path to brand directory.
     *
     * @param brand string indicating which brand directory to return.
     */
    public Path getBrandDir(String brand) {
      return this.BRAND_DIRS.get(brand);
    }
    
    /**
     * Returns brand suffix based on full brand name.
     *
     * @param brand string indicating which brand suffix to return.
     */
    public String[] getBrandSuffix(String brand) {
      return this.BRAND_SUFFIX.get(brand);
    }
       
}