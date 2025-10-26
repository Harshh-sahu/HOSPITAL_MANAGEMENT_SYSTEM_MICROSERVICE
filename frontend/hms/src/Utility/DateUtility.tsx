function formatDate(dateString: any) {
  
    const months = [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    ];

    if (!dateString) return undefined;

    const date = new Date(dateString);
    const day = date.getDate();
    const month = months[date.getMonth()];
    const year = date.getFullYear();

    return `${day} ${month} ${year}`;
}


const formatDateWithTime = (dateString: any) => {
    if (!dateString) return undefined;

    const date = new Date(dateString);

    // Format options
    const options: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "short",   // "Jan", "Feb", etc. | use "long" for full name
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true      // 12-hour format with AM/PM, use false for 24-hour
    };

    return date.toLocaleString("en-US", options);
};

const extractTimein12HourFormat = (dateString: any) => {
    if (!dateString) return undefined;
    const date = new Date(dateString);

    const options: Intl.DateTimeFormatOptions = {
        hour: "numeric",
        minute: "numeric",
        hour12: true,
     

    };

    return date.toLocaleString("en-US", options);
};

export { formatDate, formatDateWithTime, extractTimein12HourFormat };