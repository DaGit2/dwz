package com.wework.websitesearcher.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultAggregator {
    private final AtomicInteger matches = new AtomicInteger();
    private final AtomicInteger unmatches = new AtomicInteger();
    private final Pattern pattern;
    private final AtomicInteger total = new AtomicInteger();
    private final AtomicInteger error = new AtomicInteger();

    public ResultAggregator(String regex){
        pattern = Pattern.compile(regex);
    }

    public void parseStream(BufferedReader reader, int Id ) throws IOException{
        String line = reader.readLine();
        while(line!=null){

            Matcher matcher = pattern.matcher(line);
            boolean matches = matcher.matches();
            if(matches){
                System.out.println("worker #" + Id + " matches 1");
                this.matches.addAndGet(1);
                return;
            }
            line = reader.readLine();
        }
        System.out.println("worker #" + Id + " unmatches 1");
        unmatches.addAndGet(1);

    }

    public void totalPlusOne(){
        total.addAndGet(1);
    }
    public void errorPlusOne(){
        error.addAndGet(1);
    }

    public String summarize(){
        return "Total " + total.get() + " URL(s) parsed, "+ matches.get()+ " matched, "+ unmatches.get()+ " not matched, " + error.get() + " connection errors";
    }
}
