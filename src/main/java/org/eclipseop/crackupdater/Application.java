package org.eclipseop.crackupdater;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.impl.*;
import org.eclipseop.crackupdater.util.JarUtil;
import org.eclipseop.crackupdater.util.Multiplier;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;

public class Application {

  private static final List<Analyzer> analyzers =
      List.of(
          new CanvasAnalyzer(),
          new GameEngineAnalyzer(),
          new MouseRecorderAnalyzer(),
          new NodeAnalyzer(),
          new BufferAnalyzer(),
          new DoublyNodeAnalyzer(),
          new WorldAnalyzer(),
          new AnimationSequenceAnalyzer(),
          new NamePairAnalyzer(),
          new EntityAnalyzer(),
          new PathingEntityAnalyzer(),
          new PlayerAppearanceAnalyzer(),
          new PlayerAnalyzer(),
          new GrandExchangeOfferAnalyzer(),
          new ClientAnalyzer());

  private static long timedFunction(Runnable runnable) {
    long startTime = System.currentTimeMillis();
    runnable.run();
    return System.currentTimeMillis() - startTime;
  }

  public static void main(String[] args) throws IOException {
    List<ClassNode> unpackedJar =
        JarUtil.unpack(new JarFile("C:\\Users\\guest_vxnzhf5\\Downloads\\osrs-206 (1).jar"));

    long analyzerTime = timedFunction(() -> analyzers.forEach(c -> c.run(unpackedJar)));
    long multiplierTime = timedFunction(() -> Multiplier.findMultipliers(unpackedJar));

    System.out.println("Identifying Hooks " + ((double) analyzerTime / 1000));
    System.out.println("Collecting Multipliers " + ((double) multiplierTime / 1000));
    System.out.println();

    FoundStuff.print();
  }
}
