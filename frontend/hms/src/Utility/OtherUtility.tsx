

const arrayToCSV = (arr: string[]) => {
    if(!arr || arr.length === 0) return "";
    return arr.join(", ");

}

 const parseToArray = (value: any) => {
  if (!value) return [];
  if (Array.isArray(value)) return value;
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed : [String(parsed)];
  } catch (e) {
    return [String(value)];
  }
};

const capitalizeFirstLetter = (str: string) => {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

const addZeroMonths=(data:any[],monthKey:string,value:string)=>{


  const months=["January","February","March","April","May","June","July","August","September","October","November","December"];

  const result = months.map((month)=>{
    const found = data.find((item)=> item[monthKey] === month);
    return found ? found : { [monthKey]: month, [value]: 0 };
  });

  return result;
}
  
  const convertReasonChartData = (data: any[]) => {
    
    const colors = ['#4caf50', '#2196f3', '#ff9800', '#e91e63', '#9c27b0', '#00bcd4', '#ffc107', '#8bc34a'];
    
    return data.map((item, index) => ({
      name: item.reason,
      value: item.count,
      color: colors[index % colors.length],
    }));
  }

  export { arrayToCSV ,parseToArray,capitalizeFirstLetter,addZeroMonths,convertReasonChartData};