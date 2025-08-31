const bloodGroups = [ { value: "A_POSITIVE", label: "A+" }, { value: "A_NEGATIVE", label: "A-" }, { value: "B_POSITIVE", label: "B+" }, { value: "B_NEGATIVE", label: "B-" }, { value: "AB_POSITIVE", label: "AB+" }, { value: "AB_NEGATIVE", label: "AB-" }, { value: "O_POSITIVE", label: "O+" }, { value: "O_NEGATIVE", label: "O-" }, ];

const bloodGroup: Record<string, string> = {
  A_POSITIVE: "A+",
  A_NEGATIVE: "A-",
  B_POSITIVE: "B+",
  B_NEGATIVE: "B-",
  AB_POSITIVE: "AB+",
  AB_NEGATIVE: "AB-",
  O_POSITIVE: "O+",
  O_NEGATIVE: "O-",
};

const doctorSpecializations =[
  "Cardiology",
  "Neurology",
  "Orthopedics",
  "Pediatrics",
  "Dermatology",
  "General Surgery",
  "Psychiatry",
  "Ophthalmology",
  "ENT",
  "Gynecology",
  "Oncology",
  "Urology",
  "Nephrology",
  "Gastroenterology",
  "Endocrinology",
  "Pulmonology",
  "Rheumatology",
  "Anesthesiology",
  "Pathology",
  "Radiology"
]


const doctorDepartments = [
  "Cardiology",
  "Neurology",
  "Orthopedics",
  "Pediatrics",
  "Dermatology",
  "General Surgery",
  "Psychiatry",
  "Ophthalmology",
  "ENT",
  "Gynecology",
  "Oncology",
  "Urology",
  "Nephrology",
  "Gastroenterology",
  "Endocrinology",
  "Pulmonology",
  "Rheumatology",
  "Anesthesiology",
  "Pathology",
  "Radiology",
  "Hematology",
  "Immunology",
  "Infectious Diseases",
  "Plastic Surgery",
  "Vascular Surgery",
  "Orthodontics",
  "Sports Medicine",
  "Critical Care",
  "Emergency Medicine"
]
const appointmentReason = [
  "Maternity / Prenatal Checkup",
  "Pediatric Consultation",
  "General Health Checkup",
  "Follow-up Visit",
  "Chronic Disease Management",
  "Post-Surgery Follow-up",
  "Cardiology Consultation",
  "Orthopedic Consultation",
  "Dermatology (Skin Issues)",
  "ENT (Ear, Nose, Throat)",
  "Neurology Consultation",
  "Gynecology Consultation",
  "Ophthalmology (Eye Checkup)",
  "Dental Checkup",
  "Psychiatry / Counseling",
  "Physiotherapy Session",
  "Immunization / Vaccination",
  "Lab Test / Diagnostic Report Review",
  "Diet & Nutrition Consultation",
  "Emergency / Urgent Care"
];
const symptoms = [
  "Fever",
  "Cough",
  "Headache",
  "Sore throat",
  "Body pain",
  "Fatigue",
  "Nausea"
];

const tests = [
  "Blood Test",
  "X-Ray",
  "Urine Test",
  "ECG",
  "MRI Scan",
  "CT Scan",
  "COVID-19 Test"
];
const dosageFrequencies = [
  "1-0-0", // Once in the morning
  "0-1-0", // Once in the afternoon
  "0-0-1", // Once at night
  "1-0-1", // Morning and night
  "1-1-0", // Morning and afternoon
  "0-1-1", // Afternoon and night
  "1-1-1", // Morning, afternoon, and night (thrice daily)
  "2-0-2", // Two tablets morning and night
  "1-1-1-1" // Four times a day (morning-afternoon-evening-night)
];
const routess=["Intravenous","oral","Topical","Inhalation"]

const medicineTypes = [
  "Tablet",
  "Capsule",
  "Syrup",
  "Injection",
  "Drops",
  "Inhaler",
  "Ointment",
  "Cream",
  "Gel",
  "Powder",
  "Spray",
  "Suppository",
  "Lozenge",
  "Solution",
  "Suspension",
  "Patch",
  "Ampoule",
  "Vial"
];

export {bloodGroups,doctorSpecializations,doctorDepartments,bloodGroup,appointmentReason,symptoms,tests,dosageFrequencies ,routess,medicineTypes}
