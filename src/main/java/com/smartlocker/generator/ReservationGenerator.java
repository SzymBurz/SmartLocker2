package com.smartlocker.generator;

import com.smartlocker.domain.Reservation;
import com.smartlocker.domain.User;
import com.smartlocker.repository.LockerRepo;
import com.smartlocker.repository.ReservationRepo;
import com.smartlocker.service.LogingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;

import static java.util.Optional.*;

@NoArgsConstructor
@Component
public class ReservationGenerator {

    @Autowired
    ReservationRepo reservationRepo;

    @Autowired
    LogingService logingService;

    @Autowired
    LockerRepo lockerRepo;

    public void generateExampleReservationData(){

        System.out.println("Generating random reservations: ");
        Random random = new Random();
        int dataSize = 100;
        LocalDateTime now = LocalDateTime.now();
        User user = logingService.generateUserForDemo();
        List<Reservation> list = reservationRepo.getReservationsList();
        for (int i = 1; i < dataSize; i ++) {

            int lockerNo = random.nextInt(99);

            if(findLastReservationWithLockerId(reservationRepo.getReservationsList(), lockerNo).isPresent()) {

                LocalDateTime end = findLastReservationWithLockerId(reservationRepo.getReservationsList(), lockerNo).get().getEnd();
                LocalDateTime startOfNew = end.plusHours((long) random.nextInt(2)+1);
                reservationRepo.add(
                new Reservation(startOfNew, generateEndTime(startOfNew), user, lockerRepo.getLockerById(lockerNo)));
            } else {
                reservationRepo.add(
                new Reservation(generateStartTime(now), generateEndTime(now), user, lockerRepo.getLockerById(lockerNo)));
            }
        }
    }

    private static LocalDateTime generateStartTime(LocalDateTime now) {
        Random random = new Random();
        LocalDateTime output = now;
        output = output.minusHours(3L);
        output = output.plusHours((long) random.nextInt(3));
        return output;
    }

    private static LocalDateTime generateEndTime(LocalDateTime now) {
        Random random = new Random();
        LocalDateTime output = now;
        output = output.plusHours(1L);
        output = output.plusHours((long) random.nextInt(3));
        return output;
    }

    private static Optional<Reservation> findLastReservationWithLockerId(List<Reservation> list, int lockerNo) {
        Optional<Reservation> output = empty();
        ListIterator<Reservation> listIterator = list.listIterator(list.size());
        while(listIterator.hasPrevious()) {
            Reservation previous = listIterator.previous();
            if(previous.getLocker().getId() == lockerNo) {
                output = ofNullable(previous);
                break;
            }
        }
        return  output;
    }
}

