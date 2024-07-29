import React, { useState } from 'react';
import Calendar from 'react-calendar';
import './CustomCalendar.css';

const CustomCalendar = () => {
  const [date, setDate] = useState(new Date());

  const onChange = (newDate) => {
    setDate(newDate);
  };

  return (
    <div className="calendar-container">
      <Calendar
        onChange={onChange}
        value={date}
        tileContent={({ date, view }) =>
          view === 'month' && date.getDate() === new Date().getDate() ? (
            <span className="highlight"></span>
          ) : null
        }
        next2Label={null}
        prev2Label={null}
        showNeighboringMonth={false}
      />
      <div className="selected-date">
        {date.toLocaleDateString('ko-KR', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
        })}
      </div>
    </div>
  );
};

export default CustomCalendar;
