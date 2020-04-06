import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Zing
 * @date 2020-03-31
 */
interface FileCopyRunner {
    void copyFile(File source, File target);
}

public class FileCopyDemo {

    private static final int ROUNDS = 5;

    private static void benchmark(FileCopyRunner runner, File source, File target) {
        long elapse = 0;
        for (int i = 0; i < ROUNDS; i++) {
            long startTime = System.currentTimeMillis();
            runner.copyFile(source, target);
            elapse += System.currentTimeMillis() - startTime;
            target.delete();
        }
        System.out.println(runner + ": " + elapse / ROUNDS);
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream in = null;
                OutputStream out = null;

                try {
                    in = new FileInputStream(source);
                    out = new FileOutputStream(target);

                    int result = -1;
                    while ((result = in.read()) != -1) {
                        out.write(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(in);
                    close(out);
                }
            }

            @Override
            public String toString() {
                return "noBufferStreamCopy";
            }
        };

        FileCopyRunner bufferedStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream in = null;
                OutputStream out = null;

                try {
                    in = new BufferedInputStream(new FileInputStream(source));
                    out = new BufferedOutputStream(new FileOutputStream(target));

                    byte[] buffer = new byte[1024];

                    int result = -1;
                    while ((result = in.read(buffer)) != -1) {
                        out.write(buffer, 0, result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(in);
                    close(out);
                }
            }

            @Override
            public String toString() {
                return "bufferedStreamCopy";
            }
        };

        FileCopyRunner nioBufferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel in = null;
                FileChannel out = null;

                try {
                    in = new FileInputStream(source).getChannel();
                    out = new FileOutputStream(target).getChannel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (in.read(buffer) != -1) {
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            out.write(buffer);
                        }
                        buffer.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(in);
                    close(out);
                }
            }

            @Override
            public String toString() {
                return "nioBufferCopy";
            }
        };

        FileCopyRunner nioTransferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel in = null;
                FileChannel out = null;

                try {
                    in = new FileInputStream(source).getChannel();
                    out = new FileOutputStream(target).getChannel();
                    long total = in.size();
                    long written = 0;
                    while (written != total) {
                        written += in.transferTo(0, total, out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(in);
                    close(out);
                }
            }

            @Override
            public String toString() {
                return "nioTransferCopy";
            }
        };

        File source = new File("test");
        File target = new File("test.copy");
        benchmark(noBufferStreamCopy, source, target);
        benchmark(bufferedStreamCopy, source, target);
        benchmark(nioBufferCopy, source, target);
        benchmark(nioTransferCopy, source, target);
    }
}

