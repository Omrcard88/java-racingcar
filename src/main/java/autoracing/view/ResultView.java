package autoracing.view;

import autoracing.domain.Car;
import autoracing.domain.RacingGame;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static autoracing.domain.AssertUtils.assertNotNull;

public class ResultView {
    private static final int NOT_INITIALIZED = -1;

    private final RacingGame racingGame;
    private final String resultTitle;
    private final WinnersRenderer winnersRenderer;
    private final CarTrackRenderer carTrackRenderer;
    private int maxNameSize = NOT_INITIALIZED;

    public static class Builder {
        private static final String DEFAULT_RESULT_TITLE = "실행 결과";
        private static final WinnersRenderer DEFAULT_WINNERS_RENDERER = new WinnersRenderer("%s가 최종 우승했습니다.", ", ");
        private static final CarTrackRenderer DEFAULT_CAR_TRACK_RENDERER = new CarTrackRenderer("-", " : ", "-");

        private static final String NULL_RACING_GAME = "racingGame must be not null.";
        private static final String NULL_RESULT_TITLE = "resultTitle must be not null.";
        private static final String NULL_WINNERS_RENDERER = "winnersRenderer must be not null.";
        private static final String NULL_CAR_TRACK_RENDERER = "carTrackRenderer must be not null.";

        private final RacingGame racingGame;
        private String resultTitle;
        private WinnersRenderer winnerRenderer;
        private CarTrackRenderer carTrackRenderer;

        public Builder(RacingGame racingGame) {
            assertNotNull(racingGame, NULL_RACING_GAME);
            this.racingGame = racingGame;
            this.resultTitle = DEFAULT_RESULT_TITLE;
            this.winnerRenderer = DEFAULT_WINNERS_RENDERER;
            this.carTrackRenderer = DEFAULT_CAR_TRACK_RENDERER;
        }

        public Builder resultTitle(String resultTitle) {
            assertNotNull(resultTitle, NULL_RESULT_TITLE);
            this.resultTitle = resultTitle;
            return this;
        }

        public Builder winnersRenderer(WinnersRenderer winnersRenderer) {
            assertNotNull(winnersRenderer, NULL_WINNERS_RENDERER);
            this.winnerRenderer = winnersRenderer;
            return this;
        }

        public Builder carTrackRenderer(CarTrackRenderer carTrackRenderer) {
            assertNotNull(carTrackRenderer, NULL_CAR_TRACK_RENDERER);
            this.carTrackRenderer = carTrackRenderer;
            return this;
        }

        public ResultView build() {
            return new ResultView(racingGame, resultTitle, winnerRenderer, carTrackRenderer);
        }
    }

    public ResultView(RacingGame racingGame, String resultTitle, WinnersRenderer winnersRenderer, CarTrackRenderer carTrackRenderer) {
        this.racingGame = racingGame;
        this.resultTitle = resultTitle;
        this.winnersRenderer = winnersRenderer;
        this.carTrackRenderer = carTrackRenderer;
    }

    public void show() {
        calculateMaxNameSizeOnlyOnce();
        print();
    }

    private void calculateMaxNameSizeOnlyOnce() {
        if (maxNameSize == NOT_INITIALIZED) {
            calculateMaxNameSize();
        }
    }

    private void calculateMaxNameSize() {
        this.maxNameSize = racingGame.getParticipants().stream()
                .map(participant -> participant.getName().length())
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("participants must not be empty."));
    }

    private void print() {
        System.out.println(resultTitle);
        printRace();
        printAnnouncingWinners();
    }

    private void printRace() {
        IntStream.rangeClosed(0, racingGame.getTotalRounds())
                .forEach(round -> System.out.println(renderRound(round, "\n")));

    }

    private String renderRound(int round, String divider) {
        return racingGame.getParticipants().stream()
                .map(car -> renderCarTrack(car, round))
                .collect(Collectors.joining("\n"))
                + divider;
    }

    private String renderCarTrack(Car car, int round) {
        return carTrackRenderer.render(car, round, maxNameSize);
    }

    private void printAnnouncingWinners() {
        System.out.println(renderAnnouncingWinners());
    }

    private String renderAnnouncingWinners() {
        return winnersRenderer.render(racingGame.getWinners());
    }
}
