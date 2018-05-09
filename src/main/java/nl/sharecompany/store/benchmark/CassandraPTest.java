package nl.sharecompany.store.benchmark;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.store.csp.DfaFactory.FixDfaFactory;
import nl.sharecompany.store.csp.command.EndOfFixCommand;
import nl.sharecompany.store.csp.message.FixMessage;
import nl.sharecompany.store.db.BulkLoader;
import org.openjdk.jmh.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.infra.Blackhole;

public class CassandraPTest {

    @State(Scope.Thread)
    public static class DfaState {

        private static final int BUFFER_SIZE = 1572864;
        private static final int BATCH_LIMIT = 60;

        private BulkLoader loader;

        private FixMessage fixMessage;
        private EndOfFixCommand endOfFixCommand = new EndOfFixCommand(fixMessage, BATCH_LIMIT, loader);

        @Setup(Level.Trial)
        public void doSetup(final Blackhole bh) {
            this.fixMessage =  new FixMessage();
            this.loader = bh::consume;
            this.endOfFixCommand = new EndOfFixCommand(fixMessage, BATCH_LIMIT, loader);
            this.dfa = new FixDfaFactory(endOfFixCommand, fixMessage).create();
        }

        public ArrayDFA dfa;
    }

    @State(Scope.Benchmark)
    public static class Data {
        private static final int BUFFER_SIZE = 1572864;


        public final byte[] buffer = new byte[BUFFER_SIZE];
        public int size;

        @Setup(Level.Trial)
        public void doSetup(){
            try(FileInputStream data = new FileInputStream("")) {
                size = data.read(buffer, 0, buffer.length);

            } catch (IOException e) {
                System.err.println("IO Error " + e);
            }
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public void dfaSingleBufferTest(DfaState state, Data data) {
        state.dfa.parse(data.buffer, 0, data.size);
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public void dfaFullBufferTest(DfaState state, Data data) {

    }

    public void cassandraBulkLoaderTest(){
        // Test the performance of writing to the cluster.
    }
}
