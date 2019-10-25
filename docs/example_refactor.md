# Example Refactor

## Before

    public final void run() {
        if (loadingStep == 1) {
            loadingStep = 2;
            graphics = getGraphics();
            load_jagex();
            draw_loading_screen(0, "Loading...");
            startGame();
            loadingStep = 0;
        }
        int i = 0;
        int j = 256;
        int sleep = 1;
        int i1 = 0;
        for (int j1 = 0; j1 < 10; j1++)
            timings[j1] = System.currentTimeMillis();

        //long l = System.currentTimeMillis();
        while (stop_timeout >= 0) {
            if (stop_timeout > 0) {
                stop_timeout--;
                if (stop_timeout == 0) {
                    close_program();
                    appletThread = null;
                    return;
                }
            }
            int k1 = j;
            int last_sleep = sleep;
            j = 300;
            sleep = 1;
            long time = System.currentTimeMillis();
            if (timings[i] == 0L) {
                j = k1;
                sleep = last_sleep;
            } else if (time > timings[i])
                j = (int) ((long) (2560 * anInt4) / (time - timings[i]));
            if (j < 25)
                j = 25;
            if (j > 256) {
                j = 256;
                sleep = (int) ((long) anInt4 - (time - timings[i]) / 10L);
                if (sleep < threadSleep)
                    sleep = threadSleep;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException _ex) {
            }
            timings[i] = time;
            i = (i + 1) % 10;
            if (sleep > 1) {
                for (int j2 = 0; j2 < 10; j2++)
                    if (timings[j2] != 0L)
                        timings[j2] += sleep;

            }
            int k2 = 0;
            while (i1 < 256) {
                handle_inputs();
                i1 += j;
                if (++k2 > max_draw_time) {
                    i1 = 0;
                    interlace_timer += 6;
                    if (interlace_timer > 25) {
                        interlace_timer = 0;
                        interlace = true;
                    }
                    break;
                }
            }
            interlace_timer--;
            i1 &= 0xff;
            draw();
        }
        if (stop_timeout == -1)
            close_program();
        appletThread = null;
    }

## After

    public void run() {
        while (!exiting) {
            long before = System.currentTimeMillis();

            pollInput();
            tick();
            render();

            int elapsed = (int) (System.currentTimeMillis() - before);
            int sleepTime = MS_PER_FRAME - elapsed;

            if (sleepTime < 1) {
                sleepTime = 1;
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
